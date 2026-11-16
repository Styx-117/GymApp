package com.smartfit.gymApp.views;

import com.smartfit.gymApp.repository.PageResult;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class DirectoryTableViewTest {
    @FunctionalInterface
    interface Loader { PageResult<Object[]> load(String query, int page, int size) throws Exception; }

    static class Directory extends DirectoryTableView {
        final Loader loader;
        Directory(Loader loader) {
            super("Directorio", "Prueba sin base de datos", new String[]{"ID", "Nombre"});
            this.loader = loader;
        }
        protected PageResult<Object[]> fetchPage(String query, Boolean active, int page, int size) {
            try { return loader.load(query, page, size); }
            catch (Exception e) { throw new IllegalStateException(e); }
        }
        protected void selectionChanged(Object[] row) {}
        protected void updateActions() {}
        void whenDisplayed(String text, CountDownLatch latch) {
            model.addTableModelListener(e -> {
                if (model.getRowCount() > 0 && text.equals(model.getValueAt(0, 1))) latch.countDown();
            });
        }
        String displayed() { return model.getRowCount() == 0 ? "" : String.valueOf(model.getValueAt(0, 1)); }
    }

    @Test
    void duplicateNavigationSharesLoadAndManualRefreshBypassesShortCache() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        CountDownLatch started = new CountDownLatch(1), release = new CountDownLatch(1);
        Directory view = create((query, page, size) -> {
            int call = calls.incrementAndGet();
            if (call == 1) { started.countDown(); await(release); }
            return result("carga " + call, page, size, 1);
        });
        try {
            CountDownLatch first = new CountDownLatch(1);
            edt(() -> { view.whenDisplayed("carga 1", first); view.refreshData(); view.refreshData(); });
            await(started);
            assertEquals(1, calls.get());
            release.countDown(); await(first);
            edt(view::refreshData);
            assertEquals(1, calls.get(), "Volver a la misma pagina reciente no repite la consulta");
            CountDownLatch second = new CountDownLatch(1);
            edt(() -> { view.whenDisplayed("carga 2", second); button(view, "Actualizar").doClick(); });
            await(second);
            assertEquals(2, calls.get(), "Actualizar consulta incluso cuando la cache sigue vigente");
        } finally { release.countDown(); }
    }

    @Test
    void slowSupersededSearchCannotReplaceNewerRowsEvenWhenDriverIgnoresInterrupt() throws Exception {
        CountDownLatch started = new CountDownLatch(1), release = new CountDownLatch(1), finished = new CountDownLatch(1);
        AtomicBoolean staleDisplayed = new AtomicBoolean();
        Directory view = create((query, page, size) -> {
            if (query.isEmpty()) {
                started.countDown();
                boolean released = false;
                while (!released) {
                    try { released = release.await(5, TimeUnit.SECONDS); }
                    catch (InterruptedException ignored) { /* Simula un controlador que tarda en cancelar. */ }
                }
                finished.countDown();
                return result("anterior", page, size, 1);
            }
            return result(query, page, size, 1);
        });
        try {
            CountDownLatch latest = new CountDownLatch(1);
            edt(() -> {
                view.model.addTableModelListener(e -> { if ("anterior".equals(view.displayed())) staleDisplayed.set(true); });
                view.whenDisplayed("nuevo", latest);
                view.refreshData();
            });
            await(started);
            edt(() -> search(view).setText("nuevo"));
            await(latest);
            release.countDown(); await(finished); drainWorkerNotifications();
            AtomicReference<String> displayed = new AtomicReference<>();
            edt(() -> displayed.set(view.displayed()));
            assertEquals("nuevo", displayed.get());
            assertFalse(staleDisplayed.get(), "La respuesta anterior no debe mostrarse ni temporalmente");
        } finally { release.countDown(); }
    }

    @Test
    void paginationLoadsBoundedPagesAndNewSearchReturnsToFirstPage() throws Exception {
        Directory view = create((query, page, size) -> {
            assertEquals(50, size);
            return result(query + " pagina " + page, page, size, 51);
        });
        CountDownLatch first = new CountDownLatch(1), second = new CountDownLatch(1), filtered = new CountDownLatch(1);
        edt(() -> { view.whenDisplayed(" pagina 0", first); view.refreshData(); }); await(first);
        edt(() -> { view.whenDisplayed(" pagina 1", second); button(view, ">").doClick(); }); await(second);
        edt(() -> {
            assertFalse(button(view, ">").isEnabled());
            assertTrue(button(view, "<").isEnabled());
            view.whenDisplayed("filtrado pagina 0", filtered);
            search(view).setText("filtrado");
        });
        await(filtered);
        edt(() -> assertFalse(button(view, "<").isEnabled()));
    }

    private static PageResult<Object[]> result(String value, int page, int size, long total) {
        return new PageResult<>(List.<Object[]>of(new Object[]{page + 1, value}), total, page, size);
    }
    private static Directory create(Loader loader) throws Exception {
        AtomicReference<Directory> result = new AtomicReference<>(); edt(() -> result.set(new Directory(loader))); return result.get();
    }
    private static void edt(Runnable action) throws Exception { SwingUtilities.invokeAndWait(action); }
    private static void await(CountDownLatch latch) throws InterruptedException { assertTrue(latch.await(8, TimeUnit.SECONDS), "La carga no termino a tiempo"); }
    private static void drainWorkerNotifications() throws Exception {
        CountDownLatch drained = new CountDownLatch(1);
        edt(() -> { Timer timer = new Timer(200, e -> drained.countDown()); timer.setRepeats(false); timer.start(); });
        await(drained);
    }
    private static JButton button(Container root, String text) {
        for (Component c : root.getComponents()) {
            if (c instanceof JButton b && text.equals(b.getText())) return b;
            if (c instanceof Container child) { JButton b = button(child, text); if (b != null) return b; }
        }
        return null;
    }
    private static JTextField search(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JTextField f && "Buscar registros".equals(f.getAccessibleContext().getAccessibleName())) return f;
            if (c instanceof Container child) { JTextField f = search(child); if (f != null) return f; }
        }
        return null;
    }
}
