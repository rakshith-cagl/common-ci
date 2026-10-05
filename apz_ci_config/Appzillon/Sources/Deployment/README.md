CI/CD pipeline sources to build and deploy Appzillon server, web and fluentd, fluentbit components in the OPENSHIFT and EKS environment

1. apz-apps
		Please extract and follow the same folder structure to make the best use of the CI/CD pipelines
		Currently, AppzillonAdmin project is kept as reference and apache-tomcat-9.0.46 is used as the Application server
		OpenJDK 17 is supported


2. supported-jar
		JSON generator source code used as the supportive code along with Appzillon CI Build sources to generate required JSON files as input to it


3. pipeline

	1. appzillon-db
			Pipeline to run the sql DB scripts, supports databases, 'mysql', 'postgresql','oracle','mssql' 

	2. pre-Build

		nexus :
				1. Use nexus repo as the central maven repository, please refer to the settings.xml in the nexus folder
				2. Custom base images for web and server can be maintained in the nexus docker repository

	3. Post-Build / Pre-Deploy
		1. Pre required components to be maintained before the deployment activity
			
			1. Openshift : PV and PVC to be available

			2. EKS : 
				PV and PVC to be available
				And below components to be deployed before the deployment of any Appzillon application components
					1. kubernetes-dashboard
					2. load-balancer-controller - nginx / aws
					3. sealed-secrets

			3. Prometheus:
				Prometheus scrapes metrics from instrumented jobs, either directly or via an intermediary push gateway for short-lived jobs. It stores all scraped samples locally and runs rules over this data to either aggregate and record new time series from existing data or generate alerts

				The Prometheus helm chart is placed inside the pre-deploy folder and it can be deployed by using the pipeline present in the pre-deploy

			4. Grafana:
				Grafana is a visualisation tool. It is used to visualize the data collected from the promethues server.
				Promethues helm chart should be deployed before deploying grafana

				The grafana helm chart is placed inside the pre-deploy folder and it can be deployed by using the pipeline present in the pre-deploy



	4. build-deploy
			Fresh and Incremental build and deployment pipeline sources

	5. logging
			* fluentd : component is used to push the logs to elasticsearch or rsyslog
			* fluentd-cloudwatch : component is used to push the logs to aws cloud watch
			* fluentbit : component is used to push the logs to elasticsearch

	6. cleanup-workspace
			Pipeline code to cleanup the workspace used for the deployment in the openshift
		

4. infra
		Pipeline to run Terraform scripts, which creates EKS cluster in the AWS environment
