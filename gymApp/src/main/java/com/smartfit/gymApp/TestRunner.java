package com.smartfit.gymApp;

import com.smartfit.gymApp.model.Socio;
import com.smartfit.gymApp.repository.SocioRepository;
import com.smartfit.gymApp.service.SocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ⚠️ ARCHIVO TEMPORAL DE PRUEBAS ⚠️
 *
 * Se ejecuta al arrancar la aplicación. Prueba:
 *   1. Conexión a PostgreSQL (Render)
 *   2. Registro de un socio
 *   3. Listado de socios
 *   4. Búsqueda por DNI
 *   5. Eliminación del socio de prueba
 *
 * BÓRRALO CUANDO TERMINES LAS PRUEBAS.
 */
@Component
public class TestRunner implements CommandLineRunner {

    @Autowired
    private SocioService socioService;

    @Autowired
    private SocioRepository socioRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║       PRUEBAS DE CONEXIÓN Y PERSISTENCIA BD          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        // ============================================
        // 1. CONTAR SOCIOS EN LA BD
        // ============================================
        try {
            long total = socioRepository.count();
            System.out.println("✅ 1. CONEXIÓN A BD OK");
            System.out.println("      Socios actuales en BD: " + total);
        } catch (Exception e) {
            System.out.println("❌ ERROR DE CONEXIÓN: " + e.getMessage());
            return;
        }

        // ============================================
        // 2. REGISTRAR SOCIO DE PRUEBA
        // ============================================
        String dniPrueba = "99999999";
        Socio existente = socioService.buscarPorDni(dniPrueba);

        if (existente != null) {
            System.out.println();
            System.out.println("⚠️  2. Ya existe un socio con DNI " + dniPrueba);
            System.out.println("      Eliminando para hacer prueba limpia...");
            socioService.eliminar(existente.getIdSocio());
        }

        Socio nuevo = new Socio();
        nuevo.setCodigoSocio("SM-TEST-" + System.currentTimeMillis() % 10000);
        nuevo.setNombre("Test");
        nuevo.setApellido("Runner");
        nuevo.setDni(dniPrueba);
        nuevo.setTelefono("999111222");
        nuevo.setCorreo("test@runner.com");
        nuevo.setActivo(true);

        Socio guardado;
        try {
            guardado = socioService.guardar(nuevo);
            System.out.println();
            System.out.println("✅ 2. REGISTRO OK");
            System.out.println("      ID asignado:      " + guardado.getIdSocio());
            System.out.println("      Código socio:     " + guardado.getCodigoSocio());
            System.out.println("      DNI:              " + guardado.getDni());
            System.out.println("      Nombre completo:  " + guardado.getNombreCompleto());
        } catch (Exception e) {
            System.out.println("❌ ERROR AL REGISTRAR: " + e.getMessage());
            return;
        }

        // ============================================
        // 3. LISTAR SOCIOS
        // ============================================
        try {
            List<Socio> todos = socioService.listarTodos();
            System.out.println();
            System.out.println("✅ 3. LISTADO OK");
            System.out.println("      Total en BD: " + todos.size());
            System.out.println("      Últimos 5:");
            todos.stream()
                    .skip(Math.max(0, todos.size() - 5))
                    .forEach(s -> System.out.println("        - [" + s.getCodigoSocio() + "] "
                            + s.getNombreCompleto() + " (DNI: " + s.getDni() + ")"));
        } catch (Exception e) {
            System.out.println("❌ ERROR AL LISTAR: " + e.getMessage());
        }

        // ============================================
        // 4. BUSCAR POR DNI
        // ============================================
        try {
            Socio encontrado = socioService.buscarPorDni(dniPrueba);
            if (encontrado != null) {
                System.out.println();
                System.out.println("✅ 4. BÚSQUEDA OK");
                System.out.println("      Encontrado por DNI " + dniPrueba + ": "
                        + encontrado.getNombreCompleto());
            } else {
                System.out.println("❌ 4. NO SE ENCONTRÓ EL SOCIO CON DNI " + dniPrueba);
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR AL BUSCAR: " + e.getMessage());
        }

        // ============================================
        // 5. ELIMINAR SOCIO DE PRUEBA
        // ============================================
        try {
            boolean eliminado = socioService.eliminar(guardado.getIdSocio());
            if (eliminado) {
                System.out.println();
                System.out.println("✅ 5. ELIMINACIÓN OK");
                System.out.println("      Socio de prueba eliminado (ID: " + guardado.getIdSocio() + ")");
            } else {
                System.out.println("❌ 5. NO SE PUDO ELIMINAR EL SOCIO");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR AL ELIMINAR: " + e.getMessage());
        }

        // ============================================
        // RESUMEN
        // ============================================
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║              PRUEBAS FINALIZADAS                     ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Servidor listo en: http://localhost:8080");
        System.out.println("⚠️  Recuerda borrar TestRunner.java después.");
        System.out.println();
    }
}