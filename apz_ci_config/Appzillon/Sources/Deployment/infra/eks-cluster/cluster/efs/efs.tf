resource "aws_security_group" "efs-sg" {
  name        = "efsAllowTCPsg"
  description = "Allow TCP inbound traffic"
  vpc_id      = var.cluster_vpc_id

  ingress = [
    {
      description      = "TCP"
      from_port        = 2049
      to_port          = 2049
      protocol         = "tcp"
      cidr_blocks      = [var.vpc_cidr_block]
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    }
  ]

  egress = [
    {
      description      = "for all outgoing traffics"
      from_port        = 0
      to_port          = 0
      protocol         = "-1"
      cidr_blocks      = [var.vpc_cidr_block]
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    }
  ]

  tags = {
    Name = "efsAllowTCPsg"
  }
}

resource "random_id" "creation_token" {
  byte_length = 4
  prefix      = "${var.cluster_name}-"
}

#EFS File System for application logs
resource "aws_efs_file_system" "efs-log" {
  creation_token = "${random_id.creation_token.hex}-log"
  tags = {
    Name = "${var.cluster_name}-log"
  }
}

resource "aws_efs_mount_target" "efs-mt-log" {
  count           = length(var.private_subnet_cidr_blocks)
  file_system_id  = aws_efs_file_system.efs-log.id
  subnet_id       = element(var.private_subnet_cidr_blocks, count.index)
  security_groups = ["${aws_security_group.efs-sg.id}"]
}


#EFS File System for file uploads
resource "aws_efs_file_system" "efs-upload" {
  creation_token = "${random_id.creation_token.hex}-upload"
  tags = {
    Name = "${var.cluster_name}-upload"
  }
}

resource "aws_efs_mount_target" "efs-mt-upload" {
  count           = length(var.private_subnet_cidr_blocks)
  file_system_id  = aws_efs_file_system.efs-upload.id
  subnet_id       = element(var.private_subnet_cidr_blocks, count.index)
  security_groups = ["${aws_security_group.efs-sg.id}"]
}