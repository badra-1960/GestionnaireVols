package gestionnairevols;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ReservationManager extends JFrame {

    // Attributs globaux basés sur ton vrai modèle
    private final List<Reservation> reservations;
    private final Map<String, JButton> seatButtons;
    private String selectedSeatId = null;
    private Reservation lastConfirmedReservation = null;

    // Le compteur de vol de base
    private int flightCounter = 10; 

    // Composants graphiques
    private JTextField txtNom, txtNumeroVol, txtDestination;
    private JTextField txtRecherche;
    private JLabel lblSelectedSeat;

    // Palette de couleurs "Modern Premium Aviation"
    private final Color COLOR_FREE = new Color(46, 204, 113);       // Vert émeraude doux
    private final Color COLOR_OCCUPIED = new Color(231, 76, 60);    // Rouge corail
    private final Color COLOR_SELECTED = new Color(241, 196, 15);   // Jaune soleil
    private final Color COLOR_BG = new Color(236, 240, 241);       // Gris/Bleu très clair épuré
    private final Color COLOR_FUSELAGE = new Color(255, 255, 255); // Blanc cabine
    private final Color COLOR_COCKPIT = new Color(52, 73, 94);     // Bleu nuit pour le nez de l'avion

    public ReservationManager() {
        this.reservations = new ArrayList<>();
        this.seatButtons = new HashMap<>();
        
        // 1. Charger les données réelles du fichier .dat au démarrage
        loadReservationsFromFile();
        
        // Ajustement du compteur en fonction des données existantes
        this.flightCounter = 10 + this.reservations.size();
        
        // 2. Initialisation de l'interface graphique
        initUI();
    }

    // Convertit un SeatId (ex: "1A", "2B") en ID numérique pour ton modèle Reservation
    private int seatIdToId(String seatId) {
        int row = Integer.parseInt(seatId.replaceAll("[^0-9]", ""));
        String colStr = seatId.replaceAll("[0-9]", "");
        int colIndex = "ABCDEF".indexOf(colStr);
        return (row - 1) * 6 + colIndex + 1;
    }

    // Composant personnalisé pour dessiner le nez (Cockpit) de l'avion
    private class CockpitPanel extends JPanel {
        public CockpitPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 70));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(COLOR_COCKPIT);
            g2.fillArc(10, 10, w - 20, h * 2, 0, 180);

            g2.setColor(new Color(135, 211, 248)); // Bleu ciel vitré
            int[] xLeft = {w / 2 - 45, w / 2 - 40, w / 2 - 5, w / 2 - 5};
            int[] yLeft = {h - 15, h - 30, h - 30, h - 15};
            g2.fillPolygon(xLeft, yLeft, 4);

            int[] xRight = {w / 2 + 5, w / 2 + 5, w / 2 + 40, w / 2 + 45};
            int[] yRight = {h - 15, h - 30, h - 30, h - 15};
            g2.fillPolygon(xRight, yRight, 4);
        }
    }

    // Composant personnalisé pour le corps du fuselage de l'avion
    private class FuselagePanel extends JPanel {
        public FuselagePanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(COLOR_FUSELAGE);
            g2.fillRoundRect(5, 0, getWidth() - 10, getHeight(), 20, 20);

            g2.setColor(new Color(200, 214, 229));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(5, 0, getWidth() - 10, getHeight() - 1, 20, 20);
        }
    }

    private void initUI() {
        setTitle("AIR France Plane");
        setSize(1100, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblMainTitle = new JLabel("AIR FRANCE — SECTION ECONOMY", SwingConstants.CENTER);
        lblMainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblMainTitle.setForeground(new Color(44, 62, 80));
        lblMainTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblMainTitle, BorderLayout.NORTH);

        JPanel centralSplit = new JPanel(new GridLayout(1, 2, 30, 0));
        centralSplit.setOpaque(false);

        // GAUCHE : L'AVION
        JPanel leftPlaneContainer = new JPanel(new BorderLayout(0, 0));
        leftPlaneContainer.setOpaque(false);

        leftPlaneContainer.add(new CockpitPanel(), BorderLayout.NORTH);

        FuselagePanel cabinPanel = new FuselagePanel(new BorderLayout());
        cabinPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel gridSeats = new JPanel(new GridLayout(12, 7, 8, 6));
        gridSeats.setOpaque(false);

        String[] cols = {"A", "B", "C", "Aisle", "D", "E", "F"};

        for (int row = 1; row <= 12; row++) {
            for (int colIndex = 0; colIndex < 7; colIndex++) {
                if (colIndex == 3) {
                    JLabel lblRow = new JLabel(String.valueOf(row), SwingConstants.CENTER);
                    lblRow.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    lblRow.setForeground(new Color(149, 165, 166));
                    gridSeats.add(lblRow);
                } else {
                    String colLetter = cols[colIndex];
                    final String seatId = row + colLetter;

                    JButton btnSeat = new JButton(seatId);
                    btnSeat.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    btnSeat.setFocusPainted(false);
                    btnSeat.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,30), 1));
                    btnSeat.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    if (isSeatOccupied(seatId)) {
                        btnSeat.setBackground(COLOR_OCCUPIED);
                        btnSeat.setForeground(Color.WHITE);
                    } else {
                        btnSeat.setBackground(COLOR_FREE);
                        btnSeat.setForeground(Color.WHITE);
                    }

                    btnSeat.addActionListener(e -> handleSeatSelection(seatId, btnSeat));
                    seatButtons.put(seatId, btnSeat);
                    gridSeats.add(btnSeat);
                }
            }
        }
        cabinPanel.add(gridSeats, BorderLayout.CENTER);
        leftPlaneContainer.add(cabinPanel, BorderLayout.CENTER);

        JPanel bottomLeftPanel = new JPanel(new BorderLayout(0, 10));
        bottomLeftPanel.setOpaque(false);
        bottomLeftPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnDeselect = new JButton("Retirer ma sélection");
        btnDeselect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeselect.setBackground(new Color(52, 152, 219));
        btnDeselect.setForeground(Color.WHITE);
        btnDeselect.setFocusPainted(false);
        btnDeselect.setPreferredSize(new Dimension(0, 35));
        btnDeselect.addActionListener(e -> clearCurrentSelection());
        bottomLeftPanel.add(btnDeselect, BorderLayout.NORTH);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendComponent("Libre", COLOR_FREE));
        legendPanel.add(createLegendComponent("Occupé", COLOR_OCCUPIED));
        legendPanel.add(createLegendComponent("Votre Sélection", COLOR_SELECTED));
        bottomLeftPanel.add(legendPanel, BorderLayout.SOUTH);

        leftPlaneContainer.add(bottomLeftPanel, BorderLayout.SOUTH);
        centralSplit.add(leftPlaneContainer);

        // DROITE : FORMULAIRE
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setOpaque(false);

        JPanel cardForm = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(new Color(220, 225, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        cardForm.setOpaque(false);
        cardForm.setLayout(new BoxLayout(cardForm, BoxLayout.Y_AXIS));
        cardForm.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblFormTitle = new JLabel("Détails de l'enregistrement");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFormTitle.setForeground(new Color(44, 62, 80));
        cardForm.add(lblFormTitle);
        cardForm.add(Box.createVerticalStrut(25));

        JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 15, 22));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel lblSeatTag = new JLabel("Siège Sélectionné :");
        lblSeatTag.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSelectedSeat = new JLabel("Aucun — Choisissez sur la carte");
        lblSelectedSeat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSelectedSeat.setForeground(COLOR_OCCUPIED);

        JLabel lblName = new JLabel("Nom du passager :");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNom = new JTextField();

        JLabel lblFlight = new JLabel("Numéro de vol :");
        lblFlight.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        txtNumeroVol = new JTextField(String.format("BL:%06d", flightCounter));
        txtNumeroVol.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtNumeroVol.setEditable(false);
        txtNumeroVol.setBackground(new Color(245, 247, 250));

        JLabel lblDest = new JLabel("Destination :");
        lblDest.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDestination = new JTextField();

        fieldsPanel.add(lblSeatTag);
        fieldsPanel.add(lblSelectedSeat);
        fieldsPanel.add(lblName);
        fieldsPanel.add(txtNom);
        fieldsPanel.add(lblFlight);
        fieldsPanel.add(txtNumeroVol);
        fieldsPanel.add(lblDest);
        fieldsPanel.add(txtDestination);

        cardForm.add(fieldsPanel);

        // Recherche rapide
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("Rechercher une réservation :");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel searchControls = new JPanel(new BorderLayout(6, 0));
        searchControls.setOpaque(false);
        txtRecherche = new JTextField();
        JButton btnSearch = new JButton("🔍 Chercher");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> rechercherReservation(txtRecherche.getText().trim()));
        searchControls.add(txtRecherche, BorderLayout.CENTER);
        searchControls.add(btnSearch, BorderLayout.EAST);

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchControls, BorderLayout.CENTER);
        cardForm.add(Box.createVerticalStrut(12));
        cardForm.add(searchPanel);

        rightPanel.add(cardForm, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        actionPanel.setOpaque(false);

        JButton btnConfirm = new JButton("Confirmer réservation");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setBackground(new Color(46, 204, 113));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(0, 45));
        btnConfirm.addActionListener(e -> processReservation());

        JButton btnCancel = new JButton("Annuler la saisie");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setBackground(new Color(149, 165, 166));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(0, 45));
        btnCancel.addActionListener(e -> {
            txtNom.setText("");
            txtDestination.setText("");
            clearCurrentSelection();
        });

        JButton btnPrint = new JButton("Imprimer le ticket");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPrint.setBackground(new Color(142, 68, 173));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.setPreferredSize(new Dimension(0, 45));
        btnPrint.addActionListener(e -> processTicketPrinting());

        JButton btnAdmin = new JButton("⚙ Accéder à l'Espace Admin");
        btnAdmin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdmin.setBackground(COLOR_COCKPIT);
        btnAdmin.setForeground(Color.WHITE);
        btnAdmin.setFocusPainted(false);
        btnAdmin.setPreferredSize(new Dimension(0, 45));
        btnAdmin.addActionListener(e -> openAdminSpace());

        actionPanel.add(btnConfirm);
        actionPanel.add(btnCancel);
        actionPanel.add(btnPrint);
        actionPanel.add(btnAdmin); 
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        centralSplit.add(rightPanel);
        mainPanel.add(centralSplit, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void handleSeatSelection(String seatId, JButton button) {
        if (isSeatOccupied(seatId)) {
            JOptionPane.showMessageDialog(this, "Ce siège est déjà réservé.", "Siège indisponible", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (seatId.equals(selectedSeatId)) {
            clearCurrentSelection();
            return;
        }

        if (selectedSeatId != null) {
            JButton oldButton = seatButtons.get(selectedSeatId);
            if (oldButton != null) oldButton.setBackground(COLOR_FREE);
        }

        selectedSeatId = seatId;
        button.setBackground(COLOR_SELECTED);
        lblSelectedSeat.setText("SIÈGE : " + selectedSeatId);
        lblSelectedSeat.setForeground(new Color(39, 174, 96));

        flightCounter++;
        txtNumeroVol.setText(String.format("BL:%06d", flightCounter));
    }

    private void processReservation() {
        String nom = txtNom.getText().trim();
        String numVol = txtNumeroVol.getText().trim();
        String destination = txtDestination.getText().trim();

        if (selectedSeatId == null) {
            JOptionPane.showMessageDialog(this, "Validation impossible : aucun siège sélectionné.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nom.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Champs vides", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int calculatedId = seatIdToId(selectedSeatId);
        Reservation newRes = new Reservation(calculatedId, nom, "Conakry", destination, numVol);
        reservations.add(newRes);
        lastConfirmedReservation = newRes;

        saveReservationsToFile();

        JButton bookedButton = seatButtons.get(selectedSeatId);
        if (bookedButton != null) {
            bookedButton.setBackground(COLOR_OCCUPIED);
        }

        JOptionPane.showMessageDialog(this, "Réservation enregistrée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

        txtNom.setText("");
        txtDestination.setText("");
        selectedSeatId = null;
        lblSelectedSeat.setText("Aucun — Choisissez sur la carte");
        lblSelectedSeat.setForeground(COLOR_OCCUPIED);
    }

    private void openAdminSpace() {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();

        loginPanel.add(new JLabel("Utilisateur :"));
        loginPanel.add(txtUser);
        loginPanel.add(new JLabel("Mot de passe :"));
        loginPanel.add(txtPass);

        int result = JOptionPane.showConfirmDialog(
            this, 
            loginPanel, 
            "Authentification Requise", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String inputUser = txtUser.getText().trim();
        String inputPass = new String(txtPass.getPassword());

        if (!inputUser.equals("admin") || !inputPass.equals("admin123")) {
            JOptionPane.showMessageDialog(this, "Accès refusé : Identifiants incorrects !", "Erreur Sécurité", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog adminDialog = new JDialog(this, "Espace Administration — Gestion des Vols", true);
        adminDialog.setSize(750, 450);
        adminDialog.setLocationRelativeTo(this);
        adminDialog.setLayout(new BorderLayout(10, 10));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(COLOR_COCKPIT);
        JLabel lblTitle = new JLabel("LISTE DES PASSAGERS ENREGISTRÉS", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(new EmptyBorder(12, 12, 12, 12));
        pnlHeader.add(lblTitle);
        adminDialog.add(pnlHeader, BorderLayout.NORTH);

        String[] columns = {"ID interne", "Nom du Passager", "Départ", "Destination", "N° Vol", "Siège"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        for (Reservation r : reservations) {
            int id = r.getId();
            int rowNum = (id - 1) / 6 + 1;
            char colLetter = (char) ('A' + (id - 1) % 6);
            String seatLabel = rowNum + "" + colLetter;

            tableModel.addRow(new Object[]{
                r.getId(),
                r.getNomPassager(),
                r.getdepart(),
                r.getDestination(),
                r.getNumeroVol(),
                seatLabel
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        adminDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        JButton btnDelete = new JButton("Supprimer la réservation sélectionnée");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setBackground(COLOR_OCCUPIED);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(adminDialog, "Veuillez sélectionner un passager dans la liste.", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(adminDialog, "Êtes-vous sûr de vouloir supprimer cette réservation définitivement ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int targetId = (int) tableModel.getValueAt(selectedRow, 0);
                String seatLabel = (String) tableModel.getValueAt(selectedRow, 5);

                Reservation toRemove = null;
                for (Reservation r : reservations) {
                    if (r.getId() == targetId) {
                        toRemove = r;
                        break;
                    }
                }

                if (toRemove != null) {
                    reservations.remove(toRemove);
                    saveReservationsToFile();

                    JButton seatBtn = seatButtons.get(seatLabel);
                    if (seatBtn != null) {
                        seatBtn.setBackground(COLOR_FREE);
                    }

                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(adminDialog, "La réservation a été supprimée avec succès.", "Suppression effectuée", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        pnlFooter.add(btnDelete);
        adminDialog.add(pnlFooter, BorderLayout.SOUTH);

        adminDialog.setVisible(true);
    }

    private void processTicketPrinting() {
        Reservation target = null;

        if (selectedSeatId != null && isSeatOccupied(selectedSeatId)) {
            target = getReservationBySeat(selectedSeatId);
        } else if (lastConfirmedReservation != null) {
            target = lastConfirmedReservation;
        }

        if (target == null) {
            JOptionPane.showMessageDialog(this, "Aucun ticket à imprimer.", "Impression annulée", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String seatLabel = selectedSeatId;
        if (seatLabel == null) {
            int id = target.getId();
            int row = (id - 1) / 6 + 1;
            char col = (char) ('A' + (id - 1) % 6);
            seatLabel = row + "" + col;
        }

        String ticket = String.format(
                "====================================\n" +
                "         BOARDING PASS / TICKET      \n" +
                "====================================\n" +
                " VOL : %s\n" +
                " DEPART : %s\n" +
                " DESTINATION : %s\n" +
                " SIÈGE : %s (ECONOMY CLASS)\n" +
                " PASSAGER : %s\n" +
                "====================================\n" +
                "   BON VOYAGE À BORD DE AIR FRANCE!  \n" +
                "====================================",
                target.getNumeroVol(), target.getdepart(), target.getDestination(), seatLabel, target.getNomPassager()
        );

        JOptionPane.showMessageDialog(this, ticket, "Impression Ticket", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearCurrentSelection() {
        if (selectedSeatId != null) {
            JButton btn = seatButtons.get(selectedSeatId);
            if (btn != null) btn.setBackground(COLOR_FREE);
            selectedSeatId = null;
            lblSelectedSeat.setText("Aucun — Choisissez sur la carte");
            lblSelectedSeat.setForeground(COLOR_OCCUPIED);
        }
    }

    private boolean isSeatOccupied(String seatId) {
        int targetId = seatIdToId(seatId);
        for (Reservation r : reservations) {
            if (r.getId() == targetId) return true;
        }
        return false;
    }

    private Reservation getReservationBySeat(String seatId) {
        int targetId = seatIdToId(seatId);
        for (Reservation r : reservations) {
            if (r.getId() == targetId) return r;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void loadReservationsFromFile() {
        File f = new File("reservations.dat");
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                this.reservations.addAll((List<Reservation>) obj);
            }
        } catch (Exception e) {
            System.out.println("Aucune donnée préalable ou erreur de lecture.");
        }
    }

    private void saveReservationsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("reservations.dat"))) {
            oos.writeObject(new ArrayList<>(reservations));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rechercherReservation(String terme) {
        if (terme == null || terme.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un terme de recherche.", "Recherche invalide", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String t = terme.toLowerCase();
        List<Reservation> results = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getNomPassager() != null && r.getNomPassager().toLowerCase().contains(t)) {
                results.add(r);
                continue;
            }
            if (r.getNumeroVol() != null && r.getNumeroVol().toLowerCase().contains(t)) {
                results.add(r);
                continue;
            }
            if (r.getDestination() != null && r.getDestination().toLowerCase().contains(t)) {
                results.add(r);
            }
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune réservation trouvée", "Résultat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-20s | %-15s | %-10s | %-6s\n", "ID", "Passager", "Destination", "N°Vol", "Siège"));
        sb.append("---------------------------------------------------------------------\n");
        for (Reservation r : results) {
            int id = r.getId();
            int row = (id - 1) / 6 + 1;
            char col = (char) ('A' + (id - 1) % 6);
            String seat = row + "" + col;
            String name = r.getNomPassager() == null ? "" : r.getNomPassager();
            String dest = r.getDestination() == null ? "" : r.getDestination();
            String vol = r.getNumeroVol() == null ? "" : r.getNumeroVol();

            sb.append(String.format("%-5d | %-20s | %-15s | %-10s | %-6s\n", id, name, dest, vol, seat));
        }

        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(680, 320));

        JOptionPane.showMessageDialog(this, sp, "Résultats de la recherche", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createLegendComponent(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(16, 16));
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,40)));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(44, 62, 80));
        p.add(box);
        p.add(l);
        return p;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new ReservationManager().setVisible(true));
    }
}