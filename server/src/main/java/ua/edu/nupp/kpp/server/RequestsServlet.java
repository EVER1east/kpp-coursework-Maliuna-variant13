package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class RequestsServlet extends HttpServlet {

    private final RequestsService service = new RequestsService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                Requests request = new Requests();
                request.setId(id(req));
                service.delete(request);

            } else if ("changeState".equals(action)) {
                String state = req.getParameter("state");

                if (state == null || state.isBlank()) {
                    throw new IllegalArgumentException("Вкажіть новий стан заявки");
                }

                service.changeState(
                        id(req),
                        RequestState.valueOf(state.trim())
                );

            } else if ("add".equals(action) || "update".equals(action)) {
                boolean updating = "update".equals(action);
                Long requestId = updating ? id(req) : null;

                RequestState state = RequestState.NEW;

                if (updating) {
                    Requests current = service.getById(requestId);

                    if (current == null) {
                        throw new IllegalArgumentException("Заявку не знайдено");
                    }

                    state = current.getState();
                }

                Requests value = new Requests(
                        requestId,
                        longParameter(req, "roomNumber"),
                        optionalLongParameter(req, "specialistId"),
                        req.getParameter("description"),
                        state,
                        longParameter(req, "f1"),
                        longParameter(req, "f2"),
                        longParameter(req, "f3")
                );

                if (updating) {
                    service.update(value);
                } else {
                    service.add(value);
                }

            } else {
                throw new IllegalArgumentException("Невідома операція");
            }

            writeJson(resp, service.getAll());

        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp,
                           Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp,
                            Exception e) throws IOException {
        boolean clientError = e instanceof IllegalArgumentException;

        int status = clientError
                ? HttpServletResponse.SC_BAD_REQUEST
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        String message = clientError
                ? (e.getMessage() == null
                ? "Некоректний запит"
                : e.getMessage())
                : "Внутрішня помилка сервера";

        resp.setStatus(status);
        writeJson(resp, Map.of("error", message));
    }

    private long id(HttpServletRequest req) {
        return longParameter(req,"id");
    }

    private long longParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Не вказано параметр: " + name
            );
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Параметр " + name + " має бути цілим числом"
            );
        }
    }
    private Long optionalLongParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);

        if (value == null || value.isBlank()) {
            return null;
        }

        return longParameter(req, name);
    }
}
