variable "location" {
  type        = string
  description = "aws location of terraform server environment"

  #     validation {
  #     condition     = contains(["us-east-1"], var.location))
  #     error_message = "Unsupported Aws Region specified. Supported regions include only : us-east-1"
  #   }
}

variable "profile" {
  type        = string
  description = "terraform profile"
}

variable "cluster_name" {
  type        = string
  description = "aws location of terraform server environment"
}

variable "instance_type" {}
variable "cluster_vpc_id" {}
variable "cluster_oidc_issuer_url" {}
variable "vpc_cidr_block" {}
variable "private_subnet_cidr_blocks" {}
variable "public_subnet_cidr_blocks" {}
variable "dashboard_dns" {}
variable "filesystem_id" {}

variable "image_repos" {
  type        = list(any)
  description = "image registry names"
}