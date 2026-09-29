package vista;

import modelo.EmpleadoComercial;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import modelo.EmpleadoComercial;
import controlador.EmpleadoControlador;
import modelo.EmpleadoBase;

public class VentanaEmpleados extends JFrame {
    private final EmpleadoControlador controlador;

    private JTextField txtCedula;
    private JTextField txtNombre;
    private JTextField txtSalario;
    private JTextField txtBonificacion;

    private JComboBox<String> cbTipo;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JButton btnAgregar;
    private JButton btnBuscar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnHistorial;

    public VentanaEmpleados(EmpleadoControlador controlador) {

        this.controlador = controlador;

        setTitle("Gestión de Talento Humano");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        construirInterfaz();
        configurarEventos();
        cargarTabla();
        limpiarCampos();
    }

    private void inicializarComponentes() {

        txtCedula = new JTextField();
        txtNombre = new JTextField();
        txtSalario = new JTextField();
        txtBonificacion = new JTextField();

        cbTipo = new JComboBox<>(
                EmpleadoControlador.TIPOS_EMPLEADO
        );

        btnAgregar = new JButton("Agregar");
        btnBuscar = new JButton("Buscar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnHistorial = new JButton("Historial");

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "Cédula",
                        "Nombre",
                        "Tipo",
                        "Salario Base",
                        "Salario Total"
                },
                0
        );

        tabla = new JTable(modeloTabla);
    }

    private void construirInterfaz() {

        JPanel formulario = new JPanel(
                new GridLayout(5, 2, 10, 10)
        );

        formulario.add(new JLabel("Cédula:"));
        formulario.add(txtCedula);

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Salario:"));
        formulario.add(txtSalario);

        formulario.add(new JLabel("Tipo:"));
        formulario.add(cbTipo);

        formulario.add(new JLabel("Bonificación / Comisión %:"));
        formulario.add(txtBonificacion);

        JPanel botones = new JPanel();

        botones.add(btnAgregar);
        botones.add(btnBuscar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnHistorial);

        JPanel superior = new JPanel(
                new BorderLayout()
        );

        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void configurarEventos() {

        cbTipo.addActionListener(e -> {

            String tipo =
                    (String) cbTipo.getSelectedItem();

            boolean necesitaValor =
                    tipo.equals("Administrativo")
                            || tipo.equals("Comercial");

            txtBonificacion.setEnabled(necesitaValor);

            if (!necesitaValor) {
                txtBonificacion.setText("");
            }
        });

        btnAgregar.addActionListener(e -> {

            String error = controlador.agregarEmpleado(
                    txtCedula.getText(),
                    txtNombre.getText(),
                    txtSalario.getText(),
                    (String) cbTipo.getSelectedItem(),
                    txtBonificacion.getText()
            );

            if (error != null) {
                JOptionPane.showMessageDialog(
                        this,
                        error,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Empleado agregado correctamente."
            );

            cargarTabla();
            limpiarCampos();
        });

        btnBuscar.addActionListener(e -> {

            String cedula = txtCedula.getText();

            EmpleadoBase empleado =
                    controlador.buscarEmpleado(cedula);

            if (empleado == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No existe un empleado con esa cédula.",
                        "Empleado no encontrado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            txtNombre.setText(empleado.getNombre());

            txtSalario.setText(
                    String.valueOf(empleado.getSalarioBase())
            );

            cbTipo.setSelectedItem(empleado.getTipo());

            if (empleado instanceof modelo.EmpleadoAdministrativo administrativo) {
                txtBonificacion.setText(
                        String.valueOf(
                                administrativo.getBonificacion()
                        )
                );
            }else if (empleado instanceof modelo.EmpleadoComercial comercial) {

                txtBonificacion.setText(
                        String.valueOf(
                                comercial.getPorcentajeComision()
                        )
                );
            }else {
                txtBonificacion.setText("");
            }
        });

        btnActualizar.addActionListener(e -> {

            String error = controlador.actualizarEmpleado(
                    txtCedula.getText(),
                    txtNombre.getText(),
                    txtSalario.getText(),
                    (String) cbTipo.getSelectedItem(),
                    txtBonificacion.getText()
            );

            if (error != null) {
                JOptionPane.showMessageDialog(
                        this,
                        error,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Empleado actualizado correctamente."
            );

            cargarTabla();
            limpiarCampos();
        });

        btnEliminar.addActionListener(e -> {

            String cedula = txtCedula.getText();

            String error =
                    controlador.eliminarEmpleado(cedula);

            if (error != null) {
                JOptionPane.showMessageDialog(
                        this,
                        error,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Empleado eliminado correctamente."
            );

            cargarTabla();
            limpiarCampos();
        });

        btnLimpiar.addActionListener(
                e -> limpiarCampos()
        );

        btnHistorial.addActionListener(
                e -> mostrarHistorial()
        );
    }

    private void mostrarHistorial() {

        ArrayList<String> historial =
                controlador.obtenerHistorial();

        if (historial.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Aún no hay operaciones registradas."
            );

            return;
        }

        String texto = "";

        for (int i = 0; i < historial.size(); i++) {

            texto += (i + 1)
                    + ". "
                    + historial.get(i)
                    + "\n";
        }

        JOptionPane.showMessageDialog(
                this,
                texto,
                "Historial de operaciones",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void limpiarCampos() {

        txtCedula.setText("");
        txtNombre.setText("");
        txtSalario.setText("");
        txtBonificacion.setText("");

        cbTipo.setSelectedIndex(0);

        txtCedula.requestFocus();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);

        for (EmpleadoBase empleado : controlador.listarEmpleados()) {

            modeloTabla.addRow(new Object[]{
                    empleado.getCedula(),
                    empleado.getNombre(),
                    empleado.getTipo(),
                    empleado.getSalarioBase(),
                    empleado.calcularSalarioTotal()
            });
        }
    }

    public void mostrar() {
        setVisible(true);
    }
}
