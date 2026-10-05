# kubernetes-dashboard
resource "helm_release" "sealed-secrets-controller" {

  name  = "sealed-secrets-controller"
  chart = "./cluster/sealed-secrets/sealed-secrets-chart"

}