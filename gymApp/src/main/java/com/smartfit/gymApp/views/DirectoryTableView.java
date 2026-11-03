package com.smartfit.gymApp.views;

import com.smartfit.gymApp.repository.PageResult;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

/** Directorio con consultas en segundo plano, formulario desplazable y selección por modelo. */
abstract class DirectoryTableView extends JPanel {
    protected static final Color BACKGROUND = new Color(25, 26, 29);
    protected static final Color SURFACE = new Color(34, 35, 39);
    protected static final Color INPUT = new Color(44, 46, 50);
    protected static final Color ACCENT = new Color(255, 204, 0);
    protected final DefaultTableModel model;
    protected final JTable table;
    protected final JPanel form = new JPanel(new CompactWrapLayout(10, 8));
    protected final JPanel actions = new JPanel(new CompactWrapLayout(8, 6));
    private final JPanel editor = new EditorPanel();
    private final JSplitPane split;
    private final JTextField search = field();
    private final JComboBox<String> state = new JComboBox<>(new String[]{"Todos", "Activos", "Inactivos"});
    private final JLabel status = label("Cargando registros…");
    private final JButton refresh = button("Actualizar", false);
    private final JButton previous = button("<", false);
    private final JButton next = button(">", false);
    private final JLabel pagination = label("Página 1 de 1");
    private static final int PAGE_SIZE = 50;
    private static final long CACHE_NANOS = 5_000_000_000L;
    private final Timer searchDelay;
    private int loadVersion;
    private int page;
    private PageResult<Object[]> visiblePage;
    private Request pendingRequest;
    private Request completedRequest;
    private long completedAt;
    private SwingWorker<PageResult<Object[]>, Void> loadTask;
    private boolean busy;
    private String feedback = "";

    private record Request(String text, Boolean active, int page) {}

    protected DirectoryTableView(String title, String description, String[] columns) {
        super(new BorderLayout(0, 12));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel titleLabel = label(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 23));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel detail = label(description);
        detail.setForeground(new Color(178, 178, 190));
        detail.setAlignmentX(LEFT_ALIGNMENT);
        heading.add(titleLabel);
        heading.add(Box.createVerticalStrut(5));
        heading.add(detail);
        heading.add(Box.createVerticalStrut(14));

        JPanel toolbar = new JPanel(new GridBagLayout());
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(LEFT_ALIGNMENT);
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        search.setToolTipText("Buscar por código, nombre o datos de contacto");
        search.getAccessibleContext().setAccessibleName("Buscar registros");
        state.getAccessibleContext().setAccessibleName("Filtrar por estado");
        state.setBackground(INPUT);
        state.setForeground(Color.WHITE);
        state.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                JLabel item = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                item.setBackground(selected ? ACCENT : INPUT);
                item.setForeground(selected ? Color.BLACK : Color.WHITE);
                item.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return item;
            }
        });
        state.setPreferredSize(new Dimension(115, 34));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 8);
        toolbar.add(search, c);
        c.gridx = 1;
        c.weightx = 0;
        toolbar.add(state, c);
        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 0);
        toolbar.add(refresh, c);
        heading.add(toolbar);
        add(heading, BorderLayout.NORTH);

        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int column) { return column == 0 ? Integer.class : String.class; }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(SURFACE);
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(55, 57, 62));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focus, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(t, value, selected, focus, row, column);
                cell.setBackground(INPUT);
                cell.setForeground(Color.WHITE);
                cell.setFont(new Font("Segoe UI", Font.BOLD, 12));
                cell.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return cell;
            }
        });
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(i == 0 ? 55 : 125);
        }
        JScrollPane tableScroll = scroll(table);
        tableScroll.setMinimumSize(new Dimension(0, 80));
        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setBackground(BACKGROUND);
        tableArea.add(tableScroll, BorderLayout.CENTER);
        JPanel paging = new JPanel(new BorderLayout(12, 0));
        paging.setBackground(BACKGROUND);
        paging.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 8));
        JPanel pagingButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pagingButtons.setOpaque(false);
        previous.setToolTipText("Página anterior");
        previous.getAccessibleContext().setAccessibleName("Página anterior");
        next.setToolTipText("Página siguiente");
        next.getAccessibleContext().setAccessibleName("Página siguiente");
        previous.setPreferredSize(new Dimension(36, 30));
        next.setPreferredSize(new Dimension(36, 30));
        pagingButtons.add(previous);
        pagingButtons.add(pagination);
        pagingButtons.add(next);
        paging.add(status, BorderLayout.CENTER);
        paging.add(pagingButtons, BorderLayout.EAST);
        tableArea.add(paging, BorderLayout.SOUTH);
        previous.addActionListener(e -> { if (page > 0) { --page; refreshData(); } });
        next.addActionListener(e -> { if (visiblePage != null && visiblePage.hasNext()) { ++page; refreshData(); } });
        updatePaging();
        form.setOpaque(false);
        actions.setOpaque(false);
        editor.setBackground(SURFACE);
        editor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel editorContent = new JPanel(new BorderLayout(0, 8));
        editorContent.setOpaque(false);
        editorContent.add(form, BorderLayout.NORTH);
        editorContent.add(actions, BorderLayout.CENTER);
        editor.add(editorContent, BorderLayout.NORTH);
        editor.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                editor.revalidate();
            }
        });
        JScrollPane editorScroll = scroll(editor);
        editorScroll.setMinimumSize(new Dimension(0, 130));
        editorScroll.setPreferredSize(new Dimension(500, 190));
        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableArea, editorScroll);
        split.setBorder(null);
        split.setBackground(BACKGROUND);
        split.setResizeWeight(1.0);
        split.setDividerSize(3);
        com.smartfit.gymApp.utils.UI.separadorAmarillo(split);
        add(split, BorderLayout.CENTER);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                SwingUtilities.invokeLater(() -> fitEditor());
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectionChanged(selectedRow());
        });
        searchDelay = new Timer(280, e -> refreshData());
        searchDelay.setRepeats(false);
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { queueSearch(); }
            public void removeUpdate(DocumentEvent e) { queueSearch(); }
            public void changedUpdate(DocumentEvent e) { queueSearch(); }
        });
        search.addActionListener(e -> { searchDelay.stop(); page = 0; refreshData(true); });
        state.addActionListener(e -> { searchDelay.stop(); page = 0; refreshData(true); });
        refresh.addActionListener(e -> { searchDelay.stop(); refreshData(true); });
    }

    protected abstract PageResult<Object[]> fetchPage(String searchText, Boolean active, int page, int pageSize);
    protected abstract void selectionChanged(Object[] row);
    protected abstract void updateActions();

    private void fitEditor() {
        if (split.getHeight() <= 0) return;
        int desiredHeight = editor.getPreferredSize().height + 4;
        int availableHeight = Math.max(130, split.getHeight() - 150);
        int height = Math.min(desiredHeight, availableHeight);
        split.setDividerLocation(Math.max(0, split.getHeight() - height - split.getDividerSize()));
    }

    /** Public para recargar al entrar desde la navegación principal. */
    public final void refreshData() {
        refreshData(false);
    }

    private void queueSearch() {
        page = 0;
        cancelRead();
        status.setText("Buscando…");
        searchDelay.restart();
        updatePaging();
    }

    private void cancelRead() {
        ++loadVersion;
        if (loadTask != null) loadTask.cancel(true);
        loadTask = null;
        pendingRequest = null;
        updatePaging();
    }

    private void updatePaging() {
        boolean ready = !busy && pendingRequest == null && (searchDelay == null || !searchDelay.isRunning());
        previous.setEnabled(ready && visiblePage != null && visiblePage.hasPrevious());
        next.setEnabled(ready && visiblePage != null && visiblePage.hasNext());
        pagination.setText("Página " + (page + 1) + " de " + (visiblePage == null ? 1 : visiblePage.pageCount()));
    }

    private void refreshData(boolean force) {
        if (busy) return;
        String text = search.getText().trim();
        Boolean active = state.getSelectedIndex() == 0 ? null : state.getSelectedIndex() == 1;
        Request request = new Request(text, active, page);
        if (request.equals(pendingRequest)) return;
        if (!force && request.equals(completedRequest) && System.nanoTime() - completedAt < CACHE_NANOS) {
            cancelRead();
            showStatus(visiblePage);
            return;
        }
        cancelRead();
        int version = ++loadVersion;
        pendingRequest = request;
        updatePaging();
        status.setText("Consultando la base de datos…");
        loadTask = new SwingWorker<PageResult<Object[]>, Void>() {
            @Override protected PageResult<Object[]> doInBackground() { return fetchPage(text, active, request.page(), PAGE_SIZE); }
            @Override protected void done() {
                if (version != loadVersion || isCancelled()) return;
                pendingRequest = null;
                loadTask = null;
                try {
                    PageResult<Object[]> result = get();
                    if (request.page() >= result.pageCount()) {
                        page = result.pageCount() - 1;
                        refreshData(true);
                        return;
                    }
                    visiblePage = result;
                    completedRequest = request;
                    completedAt = System.nanoTime();
                    model.setRowCount(0);
                    for (Object[] row : result.items()) model.addRow(row);
                    selectionChanged(null);
                    showStatus(result);
                    feedback = "";
                } catch (CancellationException e) {
                    // Una búsqueda posterior ya solicitó otros datos.
                } catch (Exception e) {
                    visiblePage = null;
                    completedRequest = null;
                    page = 0;
                    model.setRowCount(0);
                    selectionChanged(null);
                    status.setText("Sin conexión. Pulsa Actualizar para reintentar.");
                } finally {
                    updatePaging();
                }
            }
        };
        loadTask.execute();
    }

    private void showStatus(PageResult<Object[]> result) {
        if (result == null) return;
        long first = result.items().isEmpty() ? 0 : (long) result.page() * result.pageSize() + 1;
        long last = first == 0 ? 0 : first + result.items().size() - 1;
        status.setText(first + "–" + last + " de " + result.total() + " registros"
                + (feedback.isEmpty() ? "" : " · " + feedback));
    }

    protected final Object[] selectedRow() {
        int selected = table.getSelectedRow();
        if (selected < 0) return null;
        int row = table.convertRowIndexToModel(selected);
        if (row < 0 || row >= model.getRowCount()) return null;
        Object[] values = new Object[model.getColumnCount()];
        for (int i = 0; i < values.length; i++) values[i] = model.getValueAt(row, i);
        return values;
    }

    protected final void write(String message, Callable<Boolean> operation) {
        if (busy) return;
        searchDelay.stop();
        cancelRead();
        completedRequest = null;
        setBusy(true);
        status.setText("Guardando cambios…");
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception { return operation.call(); }
            @Override protected void done() {
                setBusy(false);
                try {
                    if (!get()) {
                        warn("No se pudo guardar. Verifica la conexión, los datos y que no exista un DNI duplicado.");
                        status.setText("Los cambios no se guardaron.");
                        return;
                    }
                    feedback = message;
                    table.clearSelection();
                    selectionChanged(null);
                    refreshData(true);
                } catch (Exception e) {
                    status.setText("Los cambios no se guardaron.");
                    warn("No se pudo completar la operación. Revisa la conexión e inténtalo de nuevo.");
                }
            }
        }.execute();
    }

    protected final boolean isBusy() { return busy; }
    private void setBusy(boolean value) {
        busy = value;
        search.setEnabled(!value);
        state.setEnabled(!value);
        refresh.setEnabled(!value);
        table.setEnabled(!value);
        enableChildren(editor, !value);
        updatePaging();
        updateActions();
    }

    private void enableChildren(Container parent, boolean enabled) {
        for (Component child : parent.getComponents()) {
            child.setEnabled(enabled);
            if (child instanceof Container container) enableChildren(container, enabled);
        }
    }

    protected final void addField(String title, JComponent component) {
        addField(title, component, 230);
    }

    protected final void addField(String title, JComponent component, int width) {
        JPanel item = new JPanel(new BorderLayout(0, 5));
        item.setOpaque(false);
        JLabel text = label(title);
        text.setLabelFor(component);
        item.add(text, BorderLayout.NORTH);
        item.add(component, BorderLayout.CENTER);
        item.setPreferredSize(new Dimension(width, text.getPreferredSize().height + 5 + 32));
        form.add(item);
    }

    protected static JTextField field() {
        JTextField input = new JTextField();
        input.setMinimumSize(new Dimension(40, 34));
        input.setPreferredSize(new Dimension(170, 34));
        input.setBackground(INPUT);
        input.setForeground(Color.WHITE);
        input.setCaretColor(ACCENT);
        input.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(66, 68, 73)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return input;
    }

    protected static JButton button(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setBackground(primary ? ACCENT : INPUT);
        button.setForeground(primary ? Color.BLACK : Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(66, 68, 73)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    protected static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(225, 225, 232));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    protected final void warn(String text) {
        JOptionPane.showMessageDialog(this, text, "Revisar datos", JOptionPane.WARNING_MESSAGE);
    }

    protected final boolean confirm(String text) {
        return JOptionPane.showConfirmDialog(this, text, "Confirmar cambio de estado",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static JScrollPane scroll(JComponent content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(55, 57, 62)));
        scroll.getViewport().setBackground(SURFACE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    protected static String text(Object value) { return value == null ? "" : value.toString(); }

    /** Mantiene anchos naturales y calcula la altura de las filas al reducir la ventana. */
    private static class CompactWrapLayout extends FlowLayout {
        CompactWrapLayout(int horizontalGap, int verticalGap) {
            super(FlowLayout.LEFT, horizontalGap, verticalGap);
        }

        @Override public Dimension preferredLayoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                Container sized = target;
                while (sized.getWidth() == 0 && sized.getParent() != null) sized = sized.getParent();
                int width = sized.getWidth() > 0 ? sized.getWidth() : Integer.MAX_VALUE;
                Insets insets = target.getInsets();
                int available = Math.max(1, width - insets.left - insets.right - getHgap() * 2);
                int rowWidth = 0, rowHeight = 0, contentWidth = 0, contentHeight = 0;
                for (Component component : target.getComponents()) {
                    if (!component.isVisible()) continue;
                    Dimension preferred = component.getPreferredSize();
                    int gap = rowWidth == 0 ? 0 : getHgap();
                    if (rowWidth > 0 && rowWidth + gap + preferred.width > available) {
                        contentWidth = Math.max(contentWidth, rowWidth);
                        contentHeight += rowHeight + getVgap();
                        rowWidth = rowHeight = gap = 0;
                    }
                    rowWidth += gap + preferred.width;
                    rowHeight = Math.max(rowHeight, preferred.height);
                }
                contentWidth = Math.max(contentWidth, rowWidth);
                contentHeight += rowHeight;
                return new Dimension(contentWidth + insets.left + insets.right + getHgap() * 2,
                        contentHeight + insets.top + insets.bottom + getVgap() * 2);
            }
        }

        @Override public Dimension minimumLayoutSize(Container target) {
            return new Dimension(0, preferredLayoutSize(target).height);
        }
    }

    private static class EditorPanel extends JPanel implements Scrollable {
        EditorPanel() { super(new BorderLayout(0, 14)); }
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) { return 16; }
        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) { return 80; }
        public boolean getScrollableTracksViewportWidth() { return true; }
        public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
