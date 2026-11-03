package com.smartfit.gymApp.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Identidad de ventana compartida, independiente de los colores de Windows. */
public final class WindowChrome {
    private static final int TITLE_BAR_HEIGHT = 34;
    private static final int EDGE = 4;
    private static final Color BLACK = Color.BLACK;
    private static final BufferedImage LOGO = loadLogo();

    private WindowChrome() {}

    /** Debe llamarse al terminar de construir la ventana, antes de mostrarla. */
    public static void install(JFrame frame) {
        if (frame.getRootPane().getClientProperty("smartfit.chrome") != null) return;
        if (frame.isDisplayable()) throw new IllegalStateException("La barra debe instalarse antes de mostrar la ventana.");
        frame.setUndecorated(true);
        frame.setIconImages(windowIcons());
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) taskbar.setIconImage(iconImage(128));
        }
        Container content = frame.getContentPane();
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BLACK);
        shell.setBorder(BorderFactory.createEmptyBorder(EDGE, EDGE, EDGE, EDGE));
        shell.add(new TitleBar(frame), BorderLayout.NORTH);
        shell.add(content, BorderLayout.CENTER);
        frame.setContentPane(shell);
        frame.getRootPane().putClientProperty("smartfit.chrome", Boolean.TRUE);
        installResize(frame, shell);
    }

    private static BufferedImage loadLogo() {
        try (var input = WindowChrome.class.getResourceAsStream("/images/logoSmartFit.png")) {
            if (input == null) throw new IllegalStateException("No se encontro el logo en los recursos de la aplicacion.");
            BufferedImage image = ImageIO.read(input);
            if (image == null) throw new IllegalStateException("El logo no es una imagen valida.");
            // Ajuste visual en memoria: el PNG original conserva todos sus pixeles.
            int left=image.getWidth(),top=image.getHeight(),right=-1,bottom=-1;
            for (int y=0;y<image.getHeight();y++) for (int x=0;x<image.getWidth();x++) {
                int pixel=image.getRGB(x,y);
                if ((pixel>>>24)>32 && Math.max((pixel>>16)&255,Math.max((pixel>>8)&255,pixel&255))>64) {
                    left=Math.min(left,x);top=Math.min(top,y);right=Math.max(right,x);bottom=Math.max(bottom,y);
                }
            }
            return right<left ? image : image.getSubimage(left,top,right-left+1,bottom-top+1);
        } catch (IOException ex) { throw new IllegalStateException("No se pudo cargar el logo de SmartFit.",ex); }
    }

    private static BufferedImage iconImage(int size) {
        BufferedImage icon=new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=icon.createGraphics();
        g.setColor(BLACK);g.fillRect(0,0,size,size);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        double scale=(size*0.90)/Math.max(LOGO.getWidth(),LOGO.getHeight());
        int w=Math.max(1,(int)Math.round(LOGO.getWidth()*scale)),h=Math.max(1,(int)Math.round(LOGO.getHeight()*scale));
        g.drawImage(LOGO,(size-w)/2,(size-h)/2,w,h,null);g.dispose();return icon;
    }

    private static List<Image> windowIcons() {
        List<Image> icons=new ArrayList<>();
        for(int size:new int[]{16,20,24,32,48,64,128,256}) icons.add(iconImage(size));
        return icons;
    }

    private static boolean maximized(JFrame frame) {
        return (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
    }

    private static class TitleBar extends JPanel {
        private final JFrame frame;
        private final JButton maximize;
        private Rectangle restoreBounds;

        TitleBar(JFrame frame) {
            super(new BorderLayout());this.frame=frame;
            setBackground(BLACK);setPreferredSize(new Dimension(200,TITLE_BAR_HEIGHT));
            JLabel title=new JLabel(frame.getTitle(),new ImageIcon(iconImage(26)),SwingConstants.LEFT);
            title.setIconTextGap(9);title.setForeground(Color.WHITE);title.setFont(new Font("Segoe UI",Font.PLAIN,12));
            title.setBorder(BorderFactory.createEmptyBorder(0,7,0,12));add(title,BorderLayout.CENTER);
            JPanel controls=new JPanel(new GridLayout(1,3,1,0));controls.setBackground(BLACK);
            JButton minimize=control("Minimizar",0);
            maximize=control("Maximizar",1);
            JButton close=control("Cerrar",2);
            minimize.addActionListener(e->frame.setExtendedState(frame.getExtendedState()|JFrame.ICONIFIED));
            maximize.addActionListener(e->toggleMaximize());
            close.addActionListener(e->frame.dispatchEvent(new WindowEvent(frame,WindowEvent.WINDOW_CLOSING)));
            controls.add(minimize);controls.add(maximize);controls.add(close);add(controls,BorderLayout.EAST);
            Runnable update=()->{
                maximize.setEnabled(frame.isResizable());
                String text=maximized(frame)?"Restaurar":"Maximizar";
                maximize.setToolTipText(text);maximize.getAccessibleContext().setAccessibleName(text);maximize.repaint();
            };
            frame.addPropertyChangeListener("title",e->title.setText(frame.getTitle()));
            frame.addPropertyChangeListener("resizable",e->update.run());
            frame.addWindowStateListener(e->update.run());update.run();
            MouseAdapter move=new MouseAdapter() {
                private Point anchor,offset;
                private double ratio;
                public void mousePressed(MouseEvent e) {
                    if(!SwingUtilities.isLeftMouseButton(e))return;
                    anchor=e.getLocationOnScreen();offset=new Point(anchor.x-frame.getX(),anchor.y-frame.getY());
                    ratio=(double)offset.x/Math.max(1,frame.getWidth());
                }
                public void mouseDragged(MouseEvent e) {
                    if(anchor==null)return;
                    Point p=e.getLocationOnScreen();
                    if(maximized(frame)) {
                        if(anchor.distance(p)<5)return;
                        restore();offset=new Point((int)(frame.getWidth()*ratio),Math.min(offset.y,TITLE_BAR_HEIGHT));
                    }
                    frame.setLocation(p.x-offset.x,p.y-offset.y);
                }
                public void mouseReleased(MouseEvent e) {anchor=null;}
                public void mouseClicked(MouseEvent e) {if(SwingUtilities.isLeftMouseButton(e)&&e.getClickCount()==2)toggleMaximize();}
            };
            addMouseListener(move);addMouseMotionListener(move);title.addMouseListener(move);title.addMouseMotionListener(move);
        }

        private JButton control(String name,int kind) {
            JButton button=new JButton(new Icon() {
                public int getIconWidth(){return 14;}
                public int getIconHeight(){return 14;}
                public void paintIcon(Component c,Graphics graphics,int x,int y) {
                    Graphics2D g=(Graphics2D)graphics.create();g.translate(x,y);
                    g.setColor(c.isEnabled()?Color.BLACK:new Color(115,100,20));g.setStroke(new BasicStroke(1.5f));
                    if(kind==0)g.drawLine(2,10,12,10);
                    else if(kind==2){g.drawLine(2,2,12,12);g.drawLine(12,2,2,12);}
                    else if(maximized(frame)){g.drawRect(5,2,7,7);g.setColor(buttonBackground(c));g.fillRect(2,5,8,8);g.setColor(Color.BLACK);g.drawRect(2,5,8,8);}
                    else g.drawRect(2,2,10,10);
                    g.dispose();
                }
            }) {
                @Override protected void paintComponent(Graphics graphics) {
                    graphics.setColor(buttonBackground(this));graphics.fillRect(0,0,getWidth(),getHeight());super.paintComponent(graphics);
                }
            };
            button.setPreferredSize(new Dimension(45,TITLE_BAR_HEIGHT));button.setBackground(UI.ACENTO);
            button.setBorderPainted(false);button.setContentAreaFilled(false);button.setFocusPainted(false);button.setFocusable(false);
            button.setRolloverEnabled(true);button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setToolTipText(name);button.getAccessibleContext().setAccessibleName(name);return button;
        }

        private static Color buttonBackground(Component c) {
            if(c instanceof JButton b && (b.getModel().isRollover()||b.getModel().isPressed()))return new Color(230,181,0);
            return UI.ACENTO;
        }

        private void toggleMaximize() {
            if(!frame.isResizable())return;
            if(maximized(frame)){restore();return;}
            restoreBounds=frame.getBounds();
            GraphicsConfiguration config=frame.getGraphicsConfiguration();Rectangle area=new Rectangle(config.getBounds());
            Insets insets=Toolkit.getDefaultToolkit().getScreenInsets(config);
            area.x+=insets.left;area.y+=insets.top;area.width-=insets.left+insets.right;area.height-=insets.top+insets.bottom;
            frame.setMaximizedBounds(area);frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        }

        private void restore() {
            frame.setExtendedState(frame.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
            if(restoreBounds!=null)frame.setBounds(restoreBounds);
        }
    }

    private static void installResize(JFrame frame,JPanel shell) {
        MouseAdapter resize=new MouseAdapter() {
            private int edges;
            private Point anchor;
            private Rectangle original;
            private int edges(MouseEvent e) {
                if(!frame.isResizable()||maximized(frame))return 0;
                return (e.getX()<EDGE?1:0)|(e.getX()>=shell.getWidth()-EDGE?2:0)
                    |(e.getY()<EDGE?4:0)|(e.getY()>=shell.getHeight()-EDGE?8:0);
            }
            public void mouseMoved(MouseEvent e) {
                int cursor=switch(edges(e)) {
                    case 1->Cursor.W_RESIZE_CURSOR;case 2->Cursor.E_RESIZE_CURSOR;case 4->Cursor.N_RESIZE_CURSOR;case 8->Cursor.S_RESIZE_CURSOR;
                    case 5->Cursor.NW_RESIZE_CURSOR;case 6->Cursor.NE_RESIZE_CURSOR;case 9->Cursor.SW_RESIZE_CURSOR;case 10->Cursor.SE_RESIZE_CURSOR;
                    default->Cursor.DEFAULT_CURSOR;
                };
                shell.setCursor(Cursor.getPredefinedCursor(cursor));
            }
            public void mouseExited(MouseEvent e){if(anchor==null)shell.setCursor(Cursor.getDefaultCursor());}
            public void mousePressed(MouseEvent e){if(SwingUtilities.isLeftMouseButton(e)){edges=edges(e);if(edges!=0){anchor=e.getLocationOnScreen();original=frame.getBounds();}}}
            public void mouseDragged(MouseEvent e) {
                if(anchor==null)return;
                int dx=e.getXOnScreen()-anchor.x,dy=e.getYOnScreen()-anchor.y;
                Rectangle r=new Rectangle(original);Dimension min=frame.getMinimumSize();
                if((edges&1)!=0){r.width=Math.max(min.width,original.width-dx);r.x=original.x+original.width-r.width;}
                if((edges&2)!=0)r.width=Math.max(min.width,original.width+dx);
                if((edges&4)!=0){r.height=Math.max(min.height,original.height-dy);r.y=original.y+original.height-r.height;}
                if((edges&8)!=0)r.height=Math.max(min.height,original.height+dy);
                frame.setBounds(r);
            }
            public void mouseReleased(MouseEvent e){anchor=null;edges=0;}
        };
        shell.addMouseListener(resize);shell.addMouseMotionListener(resize);
    }
}
