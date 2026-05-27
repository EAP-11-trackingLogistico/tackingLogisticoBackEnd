package com.logistica.trackinglogistico.reports.repository;

import com.logistica.trackinglogistico.reports.dto.DelayReportResponse;
import com.logistica.trackinglogistico.reports.dto.ShipmentVolumeReportResponse;
import com.logistica.trackinglogistico.reports.dto.TransitTimeReportResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TransitTimeReportResponse.TransitTimeReportItem> findTransitTimes(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        String sql = """
                WITH delivered_events AS (
                    SELECT
                        e.idenvio,
                        e.numseguimiento,
                        e.fecharegistro,
                        MIN(ev.horaevento) AS fecha_entrega
                    FROM envio e
                    JOIN evento_logistico ev
                        ON ev.idenvio = e.idenvio
                    WHERE ev.tipoevento = 'DELIVERED'
                    AND e.fecharegistro BETWEEN ? AND ?
                    AND ev.horaevento >= e.fecharegistro
                    GROUP BY
                        e.idenvio,
                        e.numseguimiento,
                        e.fecharegistro
                )
                SELECT
                    idenvio,
                    numseguimiento,
                    fecharegistro,
                    fecha_entrega,
                    ROUND(
                        (EXTRACT(EPOCH FROM (fecha_entrega - fecharegistro)) / 3600.0)::numeric,
                        2
                    )::double precision AS horas_transito
                FROM delivered_events
                ORDER BY horas_transito DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new TransitTimeReportResponse.TransitTimeReportItem(
                        rs.getInt("idenvio"),
                        rs.getString("numseguimiento"),
                        rs.getTimestamp("fecharegistro").toLocalDateTime(),
                        rs.getTimestamp("fecha_entrega").toLocalDateTime(),
                        rs.getDouble("horas_transito")
                ),
                Timestamp.valueOf(startDate),
                Timestamp.valueOf(endDate)
        );
    }

    public List<DelayReportResponse.DelayReportItem> findDelayedShipments(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        String sql = """
                SELECT
                    e.idenvio,
                    e.numseguimiento,
                    p.estado,
                    ev.ubicacion,
                    ev.horaevento,
                    o.nombre AS operador
                FROM envio e
                JOIN paquete p
                    ON p.idpaquete = e.idpaquete
                JOIN evento_logistico ev
                    ON ev.idenvio = e.idenvio
                JOIN operador o
                    ON o.idoperador = ev.idoperador
                WHERE ev.tipoevento = 'DELAYED'
                  AND ev.horaevento BETWEEN ? AND ?
                ORDER BY ev.horaevento DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new DelayReportResponse.DelayReportItem(
                        rs.getInt("idenvio"),
                        rs.getString("numseguimiento"),
                        rs.getString("estado"),
                        rs.getString("ubicacion"),
                        rs.getTimestamp("horaevento").toLocalDateTime(),
                        rs.getString("operador")
                ),
                Timestamp.valueOf(startDate),
                Timestamp.valueOf(endDate)
        );
    }

    public List<ShipmentVolumeReportResponse.ShipmentVolumeItem> findShipmentVolume(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        String sql = """
                SELECT
                    DATE(e.fecharegistro) AS periodo,
                    COUNT(*) AS total_envios
                FROM envio e
                WHERE e.fecharegistro BETWEEN ? AND ?
                GROUP BY DATE(e.fecharegistro)
                ORDER BY periodo ASC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new ShipmentVolumeReportResponse.ShipmentVolumeItem(
                        rs.getDate("periodo").toLocalDate(),
                        rs.getLong("total_envios")
                ),
                Timestamp.valueOf(startDate),
                Timestamp.valueOf(endDate)
        );
    }
}
