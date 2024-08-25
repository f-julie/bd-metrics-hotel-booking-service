package com.amazon.ata.metrics.classroom.metrics;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

import javax.inject.Inject;

/**
 * Contains operations for publishing metrics.
 *
 * Class to store Metrics in AWS CloudWatch
 */
public class MetricsPublisher {

    private AmazonCloudWatch cloudWatch;

    /**
     * Creates a metrics publisher with the given AmazonCloudWatch.
     * @param cloudWatch AmazonCloudWatch
     */
    @Inject
    public MetricsPublisher (final AmazonCloudWatch cloudWatch) {
        this.cloudWatch = cloudWatch;
    }

    /**
     * Publishes the given metric to CloudWatch.
     *
     * @param metricName name of metric to publish.
     * @param value value of metric.
     * @param unit unit of metric.
     */
    public void addMetric(final String metricName, final double value, final StandardUnit unit) {
        final PutMetricDataRequest request = buildMetricDataRequest(metricName, value, unit);
        cloudWatch.putMetricData(request);
    }

    /**
     * Helper method that builds the PutMetricDataRequest object used to publish to CloudWatch.
     *
     * @param metricName name of metric
     * @param value value of metric
     * @param unit unit of metric.
     * @return PutMetricDataRequest
     */
    private PutMetricDataRequest buildMetricDataRequest(final String metricName, final double value,
                                                        final StandardUnit unit) {

        final Dimension service = new Dimension()
            .withName(MetricsConstants.SERVICE)
            .withValue(MetricsConstants.SERVICE_NAME);

        // right now this service is only in the US, so hardcoding US for now. When we launch in other marketplaces
        // we'll want to make this marketplace value configurable
        final Dimension marketplace = new Dimension()
            .withName(MetricsConstants.MARKETPLACE)
            .withValue(MetricsConstants.US_MARKETPLACE);

        final MetricDatum datum = new MetricDatum()
            .withMetricName(metricName)
            .withUnit(unit)
            .withValue(value)
            .withDimensions(service, marketplace);

        final PutMetricDataRequest request = new PutMetricDataRequest()
            .withNamespace(MetricsConstants.NAMESPACE)
            .withMetricData(datum);

        return request;
    }
}
