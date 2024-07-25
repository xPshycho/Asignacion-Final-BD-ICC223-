package visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import logico.DatabaseConnection;

public class InformeInscripcion extends JDialog {

    private JPanel contentPane;
    private JTextField textTotalCreditos;
    private JTextField txtTotalDeGrupos;
    private JTable tableGrupos;
    private String idEstudiante;
    private String codigoPeriodo;
    private JTable tableEstudiante;

    public InformeInscripcion(String idEstudiante, String codigoPeriodo) {
        this.idEstudiante = idEstudiante;
        this.codigoPeriodo = codigoPeriodo;
        initialize();
        loadData();
    }

    private void initialize() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1079, 788);
        setModal(true);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        contentPane.add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_1.setBounds(58, 37, 931, 542);
        panel.add(panel_1);
        panel_1.setLayout(null);

        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_3.setBounds(15, 16, 901, 112);
        panel_1.add(panel_3);
        panel_3.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        panel_3.add(scrollPane);
        
        tableEstudiante = new JTable();
        scrollPane.setViewportView(tableEstudiante);

        JPanel panel_4 = new JPanel();
        panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_4.setBounds(15, 144, 901, 353);
        panel_1.add(panel_4);
        panel_4.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();
        panel_4.add(scrollPane_1, BorderLayout.CENTER);

        tableGrupos = new JTable();
        scrollPane_1.setViewportView(tableGrupos);

        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_2.setBounds(58, 595, 931, 92);
        panel.add(panel_2);
        panel_2.setLayout(null);

        textTotalCreditos = new JTextField();
        textTotalCreditos.setEditable(false);
        textTotalCreditos.setBounds(548, 50, 120, 26);
        panel_2.add(textTotalCreditos);
        textTotalCreditos.setColumns(10);

        txtTotalDeGrupos = new JTextField();
        txtTotalDeGrupos.setEditable(false);
        txtTotalDeGrupos.setBounds(229, 50, 120, 26);
        panel_2.add(txtTotalDeGrupos);
        txtTotalDeGrupos.setColumns(10);

        JLabel lblNewLabel = new JLabel("Total de Grupos");
        lblNewLabel.setBounds(229, 16, 120, 20);
        panel_2.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Total de Creditos");
        lblNewLabel_1.setBounds(537, 15, 159, 20);
        panel_2.add(lblNewLabel_1);
    }

    private void loadData() {
        Connection conn = DatabaseConnection.getConnection();

        try {
            // Cargar Informaci�n del Estudiante
            String sqlEstudiante = "SELECT e.[ID Estudiante], CONCAT(e.[Nombre], ' ', e.[Apellido]) AS [Nombre Completo], "
                + "c.[Nombre] AS [Carrera], ? AS [Codigo Periodo] "
                + "FROM [Estudiante] e "
                + "JOIN [Carrera] c ON e.[ID Carrera] = c.[ID Carrera] "
                + "WHERE e.[ID Estudiante] = ?;";
            PreparedStatement pst = conn.prepareStatement(sqlEstudiante);
            pst.setString(1, codigoPeriodo);
            pst.setString(2, idEstudiante);
            ResultSet rs = pst.executeQuery();

            Vector<String> columnNamesEstudiante = new Vector<>();
            columnNamesEstudiante.add("ID Estudiante");
            columnNamesEstudiante.add("Nombre Completo");
            columnNamesEstudiante.add("Carrera");
            columnNamesEstudiante.add("Codigo Periodo");

            Vector<Vector<Object>> dataEstudiante = new Vector<>();
            if (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("ID Estudiante"));
                row.add(rs.getString("Nombre Completo"));
                row.add(rs.getString("Carrera"));
                row.add(rs.getString("Codigo Periodo"));
                dataEstudiante.add(row);
            }

            tableEstudiante.setModel(new javax.swing.table.DefaultTableModel(dataEstudiante, columnNamesEstudiante));

            rs.close();
            pst.close();

            // Cargar Grupos Inscritos
            String sqlGrupos = "SELECT g.[Numero Grupo], g.[Codigo Asignatura], a.[Nombre] AS [Nombre Asignatura], "
                + "a.[Creditos], g.[Horario] "
                + "FROM [Grupo Inscrito] gi "
                + "JOIN [Grupo] g ON gi.[Codigo Periodo] = g.[Codigo Periodo] AND gi.[Codigo Asignatura] = g.[Codigo Asignatura] AND gi.[Numero Grupo] = g.[Numero Grupo] "
                + "JOIN [Asignatura] a ON g.[Codigo Asignatura] = a.[Codigo] "
                + "WHERE gi.[ID Estudiante] = ? AND gi.[Codigo Periodo] = ?;";
            pst = conn.prepareStatement(sqlGrupos);
            pst.setString(1, idEstudiante);
            pst.setString(2, codigoPeriodo);
            rs = pst.executeQuery();

            Vector<String> columnNamesGrupos = new Vector<>();
            columnNamesGrupos.add("Numero Grupo");
            columnNamesGrupos.add("Codigo Asignatura");
            columnNamesGrupos.add("Nombre Asignatura");
            columnNamesGrupos.add("Creditos");
            columnNamesGrupos.add("Horario Condensado");

            Vector<Vector<Object>> dataGrupos = new Vector<>();
            int totalCreditos = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("Numero Grupo"));
                row.add(rs.getString("Codigo Asignatura"));
                row.add(rs.getString("Nombre Asignatura"));
                int creditos = rs.getInt("Creditos");
                row.add(creditos);
                row.add(rs.getString("Horario"));

                totalCreditos += creditos;
                dataGrupos.add(row);
            }

            tableGrupos.setModel(new javax.swing.table.DefaultTableModel(dataGrupos, columnNamesGrupos));
            textTotalCreditos.setText(String.valueOf(totalCreditos));
            txtTotalDeGrupos.setText(String.valueOf(dataGrupos.size()));

            // Ajustar tama�os de las columnas
            tableGrupos.getColumnModel().getColumn(0).setPreferredWidth(100);
            tableGrupos.getColumnModel().getColumn(1).setPreferredWidth(150);
            tableGrupos.getColumnModel().getColumn(2).setPreferredWidth(200);
            tableGrupos.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableGrupos.getColumnModel().getColumn(4).setPreferredWidth(350);

            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
