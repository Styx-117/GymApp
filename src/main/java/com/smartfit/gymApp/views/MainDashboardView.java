package com.smartfit.gymApp.views;

import com.smartfit.gymApp.service.AuditoriaService;
import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainDashboardView extends JFrame {
    private final CardLayout cards=new CardLayout();
    private final JPanel contenido=new JPanel(cards);
    private final Map<String,JPanel> vistas=new LinkedHashMap<>();
    private final Map<String,JButton> botones=new LinkedHashMap<>();
    private final String usuario;

    public MainDashboardView(String usuarioLogueado) {
        usuario=usuarioLogueado;setTitle("SmartFit - Panel Principal");Rectangle espacio=GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(1280,espacio.width),Math.min(850,espacio.height));setMinimumSize(new Dimension(980,620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);setLocationRelativeTo(null);setLayout(new BorderLayout());
        AuditoriaService.getInstance().activarPersistencia();
        JPanel lateral=new JPanel(new BorderLayout());lateral.setPreferredSize(new Dimension(215,800));lateral.setBackground(new Color(17,18,20));
        JPanel marca=new JPanel(new GridLayout(2,1,0,6));marca.setOpaque(false);marca.setBorder(BorderFactory.createEmptyBorder(28,22,28,12));
        JLabel nombre=new JLabel("SMARTFIT SYSTEM");nombre.setForeground(UI.ACENTO);nombre.setFont(new Font("Segoe UI",Font.BOLD,16));marca.add(nombre);
        JLabel sede=new JLabel("Operación de sede · Ica");sede.setForeground(Color.LIGHT_GRAY);sede.setFont(new Font("Segoe UI",Font.PLAIN,11));marca.add(sede);lateral.add(marca,BorderLayout.NORTH);
        JPanel menu=new JPanel();menu.setOpaque(false);menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS));menu.setBorder(BorderFactory.createEmptyBorder(4,12,10,12));
        for(String opcion:new String[]{"Inicio","Socios","Entrenadores","Clases","Reservas","Equipos","Incidencias","Auditoría","Consultas","Horarios"}) {
            JButton b=new JButton(opcion);b.setFont(new Font("Segoe UI",Font.BOLD,13));b.setForeground(Color.LIGHT_GRAY);b.setBackground(new Color(17,18,20));
            b.setHorizontalAlignment(SwingConstants.LEFT);b.setBorder(BorderFactory.createEmptyBorder(14,14,14,10));b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setMaximumSize(new Dimension(Integer.MAX_VALUE,49));
            b.addActionListener(e->mostrar(opcion));menu.add(b);menu.add(Box.createVerticalStrut(4));botones.put(opcion,b);
        }
        JScrollPane menuScroll=new JScrollPane(menu);menuScroll.setBorder(null);menuScroll.getViewport().setBackground(new Color(17,18,20));menuScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);lateral.add(menuScroll,BorderLayout.CENTER);
        JPanel sesion=new JPanel(new BorderLayout(0,12));sesion.setOpaque(false);sesion.setBorder(BorderFactory.createEmptyBorder(16,20,20,20));JLabel cuenta=new JLabel("Sesión: "+usuario);cuenta.setForeground(Color.LIGHT_GRAY);sesion.add(cuenta,BorderLayout.NORTH);
        JButton salir=UI.boton("Cerrar sesión");salir.addActionListener(e->{dispose();AuditoriaService.getInstance().setUsuarioActual(0);new LoginView().setVisible(true);});sesion.add(salir,BorderLayout.SOUTH);lateral.add(sesion,BorderLayout.SOUTH);
        add(lateral,BorderLayout.WEST);contenido.setBackground(UI.FONDO);add(contenido,BorderLayout.CENTER);mostrar("Inicio");
        com.smartfit.gymApp.utils.WindowChrome.install(this);
    }
    private void mostrar(String nombre) {
        JPanel vista=vistas.get(nombre);
        if(vista==null){vista=switch(nombre){
            case "Inicio"->new InicioView(usuario,this::mostrar);case "Socios"->new SocioView();case "Entrenadores"->new EntrenadorView();
            case "Clases"->new ClaseView();case "Reservas"->new ReservaView();case "Equipos"->new EquiposView();case "Incidencias"->new IncidenciaView();
            case "Auditoría"->new AuditoriaView();case "Consultas"->new ConsultaIndicesView();case "Horarios"->new AgendaView();default->throw new IllegalArgumentException(nombre);
        };vistas.put(nombre,vista);contenido.add(vista,nombre);}
        cards.show(contenido,nombre);
        if(vista instanceof SocioView s)s.refreshData();else if(vista instanceof EntrenadorView e)e.refreshData();
        botones.forEach((n,b)->{b.setBackground(n.equals(nombre)?UI.CAMPO:new Color(17,18,20));b.setForeground(n.equals(nombre)?UI.ACENTO:Color.LIGHT_GRAY);});
    }
}
