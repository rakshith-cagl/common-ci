# kubernetes-dashboard
resource "helm_release" "ingress-nginx-controller" {

  name      = "ingress-nginx"
  chart     = "./cluster/nginx-controller/ingress-nginx"
  namespace = var.namespace


  set {
    name  = "controller.service.annotations.service\\.beta\\.kubernetes\\.io/aws-load-balancer-type"
    value = "nlb"
    type  = "string"
  }

  set {
    name  = "controller.service.annotations.service\\.beta\\.kubernetes\\.io/aws-load-balancer-cross-zone-load-balancing-enabled"
    value = "true"
    type  = "string"
  }

  set {
    name  = "controller.kind"
    value = var.controller_kind
  }

  set {
    name  = "controller.ingressClassResource.name"
    value = var.ingress_class_name
  }

  set {
    name  = "controller.ingressClassResource.default"
    value = var.ingress_class_is_default
  }

  set {
    name  = "controller.daemonset.useHostPort"
    value = var.controller_daemonset_useHostPort
  }

  set {
    name  = "controller.service.externalTrafficPolicy"
    value = var.controller_service_externalTrafficPolicy
  }

  set {
    name  = "controller.publishService.enabled"
    value = var.publish_service
  }

  set {
    name  = "controller.resources.requests.memory"
    type  = "string"
    value = "${var.controller_request_memory}Mi"
  }

  dynamic "set" {
    for_each = local.controller_service_nodePorts
    content {
      name  = set.value.name
      value = set.value.value
    }
  }

  dynamic "set" {
    for_each = local.loadBalancerIP
    content {
      name  = set.value.name
      value = set.value.value
    }
  }

  dynamic "set" {
    for_each = local.metrics_enabled
    content {
      name  = set.value.name
      value = set.value.value
    }
  }

  dynamic "set" {
    for_each = var.additional_set
    content {
      name  = set.value.name
      value = set.value.value
      type  = lookup(set.value, "type", null)
    }
  }

}