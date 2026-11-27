package com.estigiagaming.backend.config;

import com.estigiagaming.backend.model.*;
import com.estigiagaming.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CargaDatosIniciales implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BoletaRepository boletaRepository;

    public CargaDatosIniciales(CategoriaRepository categoriaRepository,
                               ProductoRepository productoRepository,
                               UsuarioRepository usuarioRepository,
                               BoletaRepository boletaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.boletaRepository = boletaRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() == 0) {
            Categoria c1 = categoriaRepository.save(new Categoria("Periféricos", "Mouses, teclados y accesorios."));
            Categoria c2 = categoriaRepository.save(new Categoria("Monitores", "Pantallas gamer de alta fluidez."));
            Categoria c3 = categoriaRepository.save(new Categoria("Componentes", "Tarjetas de video y gabinetes."));
            Categoria c4 = categoriaRepository.save(new Categoria("Audio", "Auriculares y sonido envolvente."));

            Producto p1 = productoRepository.save(new Producto(
                    "STG-001", "Mouse Gamer Estigia V32", c1, 49990, 39990, true, 15, 5,
                    "/imagenes/producto-1.jpg", "Sensor óptico de 26.000 DPI y 58 gramos de peso."));
            productoRepository.save(new Producto(
                    "STG-002", "Teclado Mecánico Zagreus Pro", c1, 89990, 74990, true, 12, 5,
                    "/imagenes/producto-2.jpg", "Switches mecánicos lubricados y chasis de aluminio."));
            productoRepository.save(new Producto(
                    "STG-003", "Monitor Curvo Hades 240Hz", c2, 259990, 259990, false, 4, 5,
                    "/imagenes/producto-3.jpg", "Panel Fast IPS curvo de 27 pulgadas y 1ms de respuesta."));
            productoRepository.save(new Producto(
                    "STG-004", "Auriculares Melinoe Hyper", c4, 64990, 54990, true, 10, 4,
                    "/imagenes/producto-4.jpg", "Sonido envolvente 7.1 con almohadillas viscoelásticas."));
            productoRepository.save(new Producto(
                    "STG-005", "Gabinete Umbral Flame X2", c3, 79990, 79990, false, 2, 5,
                    "/imagenes/producto-5.jpg", "Chasis con vidrio templado y 4 ventiladores ARGB."));
            productoRepository.save(new Producto(
                    "STG-006", "Tarjeta Gráfica RTX 5060", c3, 389990, 359990, true, 8, 3,
                    "/imagenes/producto-6.jpg", "8GB GDDR7 con doble ventilador axial y DLSS."));

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            usuarioRepository.save(new Usuario("214089572", "Ignacio Isaí", "Henríquez Galleguillos",
                    "ign.henriquezg@duoc.cl", encoder.encode("admin123"), "ADMINISTRADOR",
                    "Metropolitana", "Lo Prado", "Av. San Pablo 5400"));
            usuarioRepository.save(new Usuario("185432104", "Maximiliano", "Pérez",
                    "max.perez@gmail.com", encoder.encode("max1234"), "VENDEDOR",
                    "Metropolitana", "Santiago", "Alameda 1230"));
            usuarioRepository.save(new Usuario("179653234", "Daniel Alonso", "Montero Valenzuela",
                    "daniel.m@profesor.duoc.cl", encoder.encode("profe123"), "ADMINISTRADOR",
                    "Metropolitana", "Providencia", "Antonio Varas 666"));
            Usuario cliente = usuarioRepository.save(new Usuario("204853210", "Mauricio", "Gomes Garcia",
                    "mauri03gomes@gmail.com", encoder.encode("cliente1"), "CLIENTE",
                    "Metropolitana", "Maipú", "Av. Pajaritos 3100"));

            Boleta boletaInicial = new Boleta();
            boletaInicial.setNumero("#20240705");
            boletaInicial.setFecha("2026-10-20");
            boletaInicial.setNombreCliente("Mauricio");
            boletaInicial.setApellidosCliente("Gomes Garcia");
            boletaInicial.setCorreoCliente("mauri03gomes@gmail.com");
            boletaInicial.setCalle("Av. Pajaritos 3100");
            boletaInicial.setDepartamento("Depto 603");
            boletaInicial.setRegion("Metropolitana");
            boletaInicial.setComuna("Maipú");
            boletaInicial.setIndicaciones("Dejar en conserjería");
            boletaInicial.setTotal(39990);
            boletaInicial.setEstado("Pagado");
            boletaInicial.setUsuario(cliente);

            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(boletaInicial);
            detalle.setProducto(p1);
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(39990);
            detalle.setSubtotal(39990);

            boletaInicial.setItems(List.of(detalle));
            boletaRepository.save(boletaInicial);
        }
    }
}