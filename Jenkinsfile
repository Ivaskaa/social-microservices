pipeline {
    agent any

    environment {
        REGISTRY = "localhost:5000"
        KUBECONFIG = "/root/.kube/config"
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Build and Deploy All Microservices') {
            steps {
                script {
                    def services = ['auth-service']  // Тестовий масив, пізніше додаси інші сервіси

                    for (s in services) {
                        echo "Building and deploying ${s}"

                        // 1. Build & Push Docker image
                        sh """
                            docker build -f ${s}/Dockerfile -t $REGISTRY/$s:latest .
                            docker push $REGISTRY/$s:latest
                        """

                        // 2. Deploy to Kubernetes
                        def deploymentFile = "${s}/k8s/deployment.yaml"
                        def deployExists = sh(script: "kubectl get deployment ${s} -n default", returnStatus: true) == 0

                        if (deployExists) {
                            echo "Deployment exists. Updating image..."
                            sh "kubectl set image deployment/${s} ${s}=$REGISTRY/$s:latest -n default"
                        } else {
                            echo "Deployment does not exist. Creating from file..."
                            sh "kubectl apply -f ${deploymentFile}"
                        }

                        // 3. Rollout status
                        sh "kubectl rollout status deployment/${s} -n default"
                    }
                }
            }
        }
    }
}