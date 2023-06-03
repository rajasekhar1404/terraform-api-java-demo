# Terraform
- Terraform is a Infrastructure as Code(IaC) platform,
- Using terraform you can create and manage the resources in different clouds provided by terraform.
- Terraform provides two ways to use it.
  - Terraform CLI,
  - Terraform Cloud
## Terraform CLI
- Download terraform application by clicking [here](https://developer.hashicorp.com/terraform/downloads?product_intent=terraform),
- It will download zip file which will contains `terraform.exe` file, unzip it and add the path to executable file in Environmental variables to access it from any directory,
- Open the terminal in the directory where you want to create the terraform configuration,
- Execute `terraform init` to initialize terraform,
- Create a `main.tf` file to provide the terraform configuration,
- In this file add the configuration data based on your requirement,
- Here is an example for launching a ec2 instance in aws:
    ```
    provider "aws" {
      region          = "us-east-1"
      access_key      = var.aws_access_key
      secret_key      = var.aws_secret_access_key
    }
    
    resource "aws_instance" "example" {
      ami           = "ami-0261755bbcb8c4a84"
      instance_type = "t2.micro"
    }
    
    output "instance_id" {
      value = aws_instance.example.id
    }
    ```
- To execute this configuration file use `terraform apply` command,
- If you want to destroy and existing plan use `terraform destroy` command.

## Terraform Cloud
- Create a account in terraform cloud by clicking [here](https://portal.cloud.hashicorp.com/sign-in),
- Terraform Cloud can be accessed from UI, CLI and API.
- CLI:
  - login into terraform cloud from the CLI using `terraform login` command,
  - Your browser will prompt you to create a api token, if you already have a token cancel it and enter it in the cli, else create a new and enter that.
  - Initialize terraform in your working directory and create a configuration file,
  - Execute `terraform apply` to create a new plan in the cloud, it will create a new resource in your cloud provider,
  - `terraform destroy` to destroy the workspace
- UI
  - You can perform all the operations from the terraform cloud UI as well,
  - You can link your workspaces with a VCS, then everytime a new commit happens, terraform triggers will activate and reruns the configuration,
- API
  - Terraform API gives you the opportunity to use all the features of terraform cloud over the api,
  - [Get account details](https://developer.hashicorp.com/terraform/cloud-docs/api-docs/account)
  - [Create workspace](https://developer.hashicorp.com/terraform/cloud-docs/api-docs/workspaces)
  - [Create variables in workspace](https://developer.hashicorp.com/terraform/cloud-docs/api-docs/variables)
  - [Create configuration version](https://developer.hashicorp.com/terraform/cloud-docs/api-docs/configuration-versions)
  - [Create a configuration](https://developer.hashicorp.com/terraform/cloud-docs/run/api)
  - [Destroy a workspace](https://developer.hashicorp.com/terraform/cloud-docs/run/modes-and-options)
  - You can fina a better example in this git repo to integrate terraform with spring boot, have a look at it.