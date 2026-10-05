variable "additional_endpoint_arns" {
  description = "Any alert endpoints, such as autoscaling, or app escaling endpoint arns that will respond to an alert"
  default     = []
}

variable "sns_topic_arn" {
  description = "An SNS topic ARN that has already been created. Its policy must already allow access from CloudWatch Alarms, or set `add_sns_policy` to `true`"
  default     = ""
}

variable "add_sns_policy" {
  description = "Attach a policy that allows the notifications through to the SNS topic endpoint"
  default     = "false"
}

variable "filesystem_id" {
  description = "The EFS file system ID that you want to monitor"
}

variable "burst_credit_balance_threshold" {
  description = "The minimum number of burst credits that a file system should have."
  default     = "10000000" // ~10 MB

  # 192 GB in Bytes (last hour where you can burst at 100 MB/sec)
}

variable "percent_io_limit_threshold" {
  description = "IO limit threshold"
  default     = "80"
}

variable "storage_limit_threshold" {
  description = "Size limit threshold"
  default     = "100000000" //100MB
}

variable "actions_alarm" {
  default     = []
  description = "A list of actions to take when alarms are triggered. Will likely be an SNS topic for event distribution."
}

variable "actions_ok" {
  default     = []
  description = "A list of actions to take when alarms are cleared. Will likely be an SNS topic for event distribution."
}

variable "cluster_name" {}