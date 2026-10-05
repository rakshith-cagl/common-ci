###############################################################################
# Kubernetes provider configuration
################################################################################

provider "kubernetes" {
  host                   = data.aws_eks_cluster.cluster.endpoint
  token                  = data.aws_eks_cluster_auth.cluster.token
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority.0.data)
}

data "aws_eks_cluster" "cluster" {
  name = module.eks.cluster_id
}

data "aws_eks_cluster_auth" "cluster" {
  name = module.eks.cluster_id
}


################################################################################
# EKS Module
################################################################################

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "17.22.0"

  cluster_name    = var.cluster_name
  cluster_version = "1.21"

  subnets = module.vpc.private_subnets
  vpc_id  = module.vpc.vpc_id

  enable_irsa = true

  #control plane logging
  cluster_enabled_log_types     = ["api", "audit", "authenticator", "controllerManager", "scheduler"]
  cluster_log_retention_in_days = 0

  tags = {
    environment = "development"
  }

  node_groups = {
    worker_grp = {
      desired_capacity = 3
      max_capacity     = 5
      min_capacity     = 2

      instance_types = ["${var.instance_type}"]

      tags = [
        {
          "key"                 = "k8s.io/cluster-autoscaler/enabled"
          "propagate_at_launch" = "false"
          "value"               = "true"
        },
        {
          "key"                 = "k8s.io/cluster-autoscaler/${var.cluster_name}"
          "propagate_at_launch" = "false"
          "value"               = "true"
        }
      ]
    }

    # ,
    # infra_grp = {
    #   desired_capacity = 1
    #   max_capacity     = 5
    #   min_capacity     = 1

    #   instance_types = ["t2.medium"]

    #   tags = [
    #           {
    #             "key"                 = "k8s.io/cluster-autoscaler/enabled"
    #             "propagate_at_launch" = "false"
    #             "value"               = "true"
    #           },
    #           {
    #             "key"                 = "k8s.io/cluster-autoscaler/${var.cluster_name}"
    #             "propagate_at_launch" = "false"
    #             "value"               = "true"
    #           }
    #         ]  
    # }

  }

  workers_additional_policies = ["arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"]
}

module "nginx-controller" {
  depends_on = [module.eks]
  source     = "./nginx-controller"
}

module "cloudwatch_metrics" {
  source       = "./cloudwatch_metrics"
  cluster_name = var.cluster_name
}

# module - efs 
module "efs" {
  source                     = "./efs"
  cluster_name               = var.cluster_name
  cluster_vpc_id             = module.vpc.vpc_id
  vpc_cidr_block             = var.vpc_cidr_block
  private_subnet_cidr_blocks = module.vpc.private_subnets
  cluster_oidc_issuer_url    = module.eks.cluster_oidc_issuer_url
}

# module - cloud watch efs alarms
module "cloudwatch_efs_alarms" {
  depends_on   = [module.efs]
  source       = "./alarms"
  cluster_name = var.cluster_name
  #filesystem_id = "${module.efs.efs_filesystem_id}"
  filesystem_id = ["${module.efs.log_filesystem_id}", "${module.efs.upload_filesystem_id}"]

}

# module - dashboard 
module "dashboard" {
  depends_on    = [module.nginx-controller]
  source        = "./dashboard"
  dashboard_dns = var.dashboard_dns
}

# module - sealed secrets controller
module "sealed_secrets" {
  depends_on = [module.dashboard]
  source     = "./sealed-secrets"
}