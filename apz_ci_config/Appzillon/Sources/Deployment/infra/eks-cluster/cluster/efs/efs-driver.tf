locals {
  k8s_efs_service_account_namespace = "kube-system"
  k8s_efs_service_account_name      = "aws-efs-csi-driver"
}

resource "helm_release" "kubernetes_efs_csi_driver" {
  name             = "aws-efs-csi-driver"
  namespace        = local.k8s_efs_service_account_namespace
  chart            = "aws-efs-csi-driver"
  repository       = "https://kubernetes-sigs.github.io/aws-efs-csi-driver/"
  version          = "2.2.0"
  create_namespace = false

  set {
    name  = "controller.serviceAccount.create"
    value = "true"
  }

  set {
    name  = "controller.serviceAccount.name"
    value = local.k8s_efs_service_account_name
  }

  set {
    name  = "controller.serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
    value = module.iam_efs_csi_driver_role.iam_role_arn
  }

  set {
    name = "node.serviceAccount.create"
    # We're using the same service account for both the nodes and controllers,
    # and we're already creating the service account in the controller config
    # above.
    value = "false"
  }

  set {
    name  = "node.serviceAccount.name"
    value = local.k8s_efs_service_account_name
  }

  set {
    name  = "node.serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
    value = module.iam_efs_csi_driver_role.iam_role_arn
  }
}


module "iam_efs_csi_driver_role" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-assumable-role-with-oidc"
  version = "~> 4.0"

  create_role                   = true
  role_name                     = "${var.cluster_name}-efs-csi-driver"
  provider_url                  = replace(var.cluster_oidc_issuer_url, "https://", "")
  role_policy_arns              = [aws_iam_policy.efs_csi_driver.arn]
  oidc_fully_qualified_subjects = ["system:serviceaccount:${local.k8s_efs_service_account_namespace}:${local.k8s_efs_service_account_name}"]
}

resource "aws_iam_policy" "efs_csi_driver" {
  name        = "${var.cluster_name}-efs-csi-driver"
  description = "Policy for the EFS CSI driver"
  policy      = data.aws_iam_policy_document.efs_csi_driver.json
}

data "aws_iam_policy_document" "efs_csi_driver" {

  statement {
    actions = [
      "elasticfilesystem:DescribeAccessPoints",
      "elasticfilesystem:DescribeFileSystems",
      "elasticfilesystem:DescribeMountTargets",
      "ec2:DescribeAvailabilityZones"
    ]
    resources = ["*"]
    effect    = "Allow"
  }

  statement {
    actions = [
      "elasticfilesystem:CreateAccessPoint"
    ]
    resources = ["*"]
    effect    = "Allow"
    condition {
      test     = "StringLike"
      variable = "aws:RequestTag/efs.csi.aws.com/cluster"
      values   = ["true"]
    }
  }

  statement {
    actions = [
      "elasticfilesystem:DeleteAccessPoint"
    ]
    resources = ["*"]
    effect    = "Allow"
    condition {
      test     = "StringEquals"
      variable = "aws:ResourceTag/efs.csi.aws.com/cluster"
      values   = ["true"]
    }
  }
}

