# kubernetes-dashboard
resource "helm_release" "kubernetes-dashboard" {

  name             = "kubernetes-dashboard"
  repository       = "https://kubernetes.github.io/dashboard/"
  chart            = "kubernetes-dashboard"
  namespace        = "kubernetes-dashboard"
  create_namespace = true

  set {
    name  = "service.type"
    value = "ClusterIP"
  }

  # set {
  #   name  = "protocolHttp"
  #   value = "true"
  # }

  set {
    name  = "ingress.enabled"
    value = true
  }

  set {
    name  = "ingress.hosts[0]"
    value = var.dashboard_dns
  }

  # set {
  #   name = "ingress.tls[0].secretName"
  #   value = "tls-secret"   
  # }

  set {
    name  = "ingress.tls[0].hosts[0]"
    value = var.dashboard_dns
  }


  # set {
  #   name  = "service.externalPort"
  #   value = 443
  # }


  set {
    name  = "replicaCount"
    value = 1
  }

  set {
    name  = "rbac.clusterReadOnlyRole"
    value = "true"
  }
}