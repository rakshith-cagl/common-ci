output "log_filesystem_id" {
  value       = aws_efs_file_system.efs-log.id
  description = "log fs id"
}

output "upload_filesystem_id" {
  value       = aws_efs_file_system.efs-upload.id
  description = "upload fs id"
}