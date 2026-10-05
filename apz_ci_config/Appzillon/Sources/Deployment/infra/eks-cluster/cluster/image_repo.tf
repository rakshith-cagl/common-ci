resource "aws_ecr_repository" "image_repo" {
  count                = length(var.image_repos)
  name                 = "${var.cluster_name}-${element(var.image_repos, count.index)}"
  image_tag_mutability = "MUTABLE"
}