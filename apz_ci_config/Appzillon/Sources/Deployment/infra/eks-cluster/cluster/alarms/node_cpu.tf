resource "aws_cloudwatch_metric_alarm" "node_cpu_utilization" {
  alarm_name          = "${var.cluster_name}-node_cpu_utilization"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = "1"
  metric_name         = "node_cpu_utilization"
  namespace           = "ContainerInsights"
  period              = "60"
  statistic           = "Average"
  threshold           = "80"
  alarm_description   = "This metric monitors ec2 cpu utilization"
  alarm_actions       = ["${local.endpoints}"]
  ok_actions          = ["${local.endpoints}"]

  dimensions = {
    ClusterName = "${var.cluster_name}"
  }
}