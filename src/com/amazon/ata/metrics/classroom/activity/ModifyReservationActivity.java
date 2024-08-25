package com.amazon.ata.metrics.classroom.activity;

import com.amazon.ata.metrics.classroom.dao.ReservationDao;
import com.amazon.ata.metrics.classroom.dao.models.UpdatedReservation;
import com.amazon.ata.metrics.classroom.metrics.MetricsConstants;
import com.amazon.ata.metrics.classroom.metrics.MetricsPublisher;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

import java.time.ZonedDateTime;
import javax.inject.Inject;

/**
 * Handles requests to modify a reservation
 */
public class ModifyReservationActivity {

    private ReservationDao reservationDao;
    private MetricsPublisher metricsPublisher;

    /**
     * Construct ModifyReservationActivity.
     * @param reservationDao Dao used for modify reservations.
     */
    @Inject
    public ModifyReservationActivity(ReservationDao reservationDao, MetricsPublisher metricsPublisher) {
        this.reservationDao = reservationDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Modifies the given reservation.
     * and updates the ModifiedReservationCount metric
     * and updates the ReservationRevenue metric
     * @param reservationId Id to modify reservations for
     * @param checkInDate modified check in date
     * @param numberOfNights modified number of nights
     * @return UpdatedReservation that includes the old reservation and the updated reservation details.
     */
    public UpdatedReservation handleRequest(final String reservationId, final ZonedDateTime checkInDate,
                                            final Integer numberOfNights) {

        // Modify the reservation
        UpdatedReservation updatedReservation = reservationDao.modifyReservation(reservationId, checkInDate,
            numberOfNights);

        // Update the ModifyReservationCount metric
        // (class-of-enum.enum-name)

        metricsPublisher.addMetric(MetricsConstants.MODIFY_COUNT, 1, StandardUnit.Count);

        // Update the ReservationRevenue metric with the total cost of the reservation
        // The updated reservation is stored in response upon return from the ReservationDao
        // and contains the original reservation and the modified reservation
        // if we subtract the totalCost from the modified reservation from the original reservation
        // we will have the difference in revenue for the metric

        // Calculate the revenue difference due to the modification
        double revenueDifference = updatedReservation.getModifiedReservation().getTotalCost()
                .subtract(updatedReservation.getOriginalReservation().getTotalCost())
                        .doubleValue();

        // Update the ReservationRevenue metric
        metricsPublisher.addMetric(MetricsConstants.RESERVATION_REVENUE, revenueDifference, StandardUnit.None);

        return updatedReservation;
    }
}
