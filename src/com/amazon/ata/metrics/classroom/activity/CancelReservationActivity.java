package com.amazon.ata.metrics.classroom.activity;

import com.amazon.ata.metrics.classroom.dao.ReservationDao;
import com.amazon.ata.metrics.classroom.dao.models.Reservation;
import com.amazon.ata.metrics.classroom.metrics.MetricsConstants;
import com.amazon.ata.metrics.classroom.metrics.MetricsPublisher;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

import javax.inject.Inject;

/**
 * Handles requests to cancel a reservation.
 */
public class CancelReservationActivity {

    private ReservationDao reservationDao;
    private MetricsPublisher metricsPublisher;

    /**
     * Constructs a CancelReservationActivity
     * @param reservationDao Dao used to update reservations.
     */
    @Inject
    public CancelReservationActivity(ReservationDao reservationDao, MetricsPublisher metricsPublisher) {
        this.reservationDao = reservationDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Cancels the given reservation.
     * and updates the CanceledReservationCount metric
     * and updates the ReservationRevenue metric
     * @param reservationId of the reservation to cancel.
     * @return canceled reservation
     */
    public Reservation handleRequest(final String reservationId) {

        // Cancel reservation
        Reservation response = reservationDao.cancelReservation(reservationId);

        // Update the CanceledReservationCount
        // (class-of-enum.enum-name)
        metricsPublisher.addMetric(MetricsConstants.CANCEL_COUNT, 1, StandardUnit.Count);

        // Update the ReservationRevenue metric with the total cost of the reservation
        // totalCost in the Reservation is negative if we lost revenue - all we need to do is store it
        // The reservation is stored in response upon return from the ReservationDao
        metricsPublisher.addMetric(MetricsConstants.RESERVATION_REVENUE, response.getTotalCost().doubleValue(), StandardUnit.None);


        return response;
    }
}
