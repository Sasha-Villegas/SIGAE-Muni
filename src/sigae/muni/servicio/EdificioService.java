package sigae.muni.servicio;

import sigae.muni.modelo.Edificio;
import sigae.muni.persistencia.EdificioDAO;
import java.sql.SQLException;
import java.util.List;

public class EdificioService {
    private EdificioDAO dao = new EdificioDAO();

    public void crearEdificio(Edificio e) throws SQLException {
        dao.insertar(e);
    }

    public List<Edificio> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Edificio buscarPorId(Long id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public void actualizarOcupacion(Long id, String ocupacion) throws SQLException {
        Edificio e = dao.buscarPorId(id);
        e.setOcupacionActual(ocupacion);
        dao.actualizar(e);
    }
}