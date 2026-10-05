provider "aws" {
  region  = var.location
  profile = var.profile
}

module "cluster" {
  source                     = "./cluster"
  cluster_name               = var.cluster_name
  instance_type              = var.instance_type
  cluster_vpc_id             = var.cluster_vpc_id
  cluster_oidc_issuer_url    = var.cluster_oidc_issuer_url
  vpc_cidr_block             = var.vpc_cidr_block
  private_subnet_cidr_blocks = var.private_subnet_cidr_blocks
  public_subnet_cidr_blocks  = var.public_subnet_cidr_blocks
  dashboard_dns              = "${var.cluster_name}-${var.dashboard_dns}"
  filesystem_id              = var.filesystem_id
  image_repos                = var.image_repos
}