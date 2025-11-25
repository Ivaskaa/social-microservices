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
                    // Список мікросервісів
                    def services = ['auth-service']  // Тут можна додати інші сервіси

                    for (s in services) {
                        echo "Building and deploying ${s}"

                        // 1. Build & Push Docker image
                        sh """
                            docker build -f ${s}/deployment/Dockerfile -t $REGISTRY/$s:latest .
                            docker push $REGISTRY/$s:latest
                        """

                        // 2. Deploy to Kubernetes
                        def deploymentFile = "${s}/k8s/deployment.yaml"
                        echo "Applying Kubernetes YAML for ${s}..."
                        sh "kubectl apply -f ${deploymentFile}"

                        // 3. Ensure new image rollout
                        sh "kubectl set image deployment/${s} ${s}=$REGISTRY/$s:latest -n default"
                        sh "kubectl rollout status deployment/${s} -n default"
                    }
                }
            }
        }
    }
}
