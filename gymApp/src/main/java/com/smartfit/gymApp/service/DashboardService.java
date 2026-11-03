package com.smartfit.gymApp.service;

import com.smartfit.gymApp.repository.DashboardDAO;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final DashboardDAO dao;
    public DashboardService(){this(new DashboardDAO());}
    @Autowired public DashboardService(DashboardDAO dao){this.dao=dao;}
    public DashboardDAO.Resumen cargar() throws SQLException {return dao.cargar();}
}
