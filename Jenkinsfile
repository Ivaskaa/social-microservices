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
                    def services = ['auth-service','config-service','discovery-service','gateway-service','notification-service','post-service','user-service']

                    for (s in services) {
                        echo "Building and deploying ${s}"
                        sh """
                            docker build -f $s/Dockerfile -t $REGISTRY/$s:latest .
                            docker push $REGISTRY/$s:latest
                            kubectl set image deployment/$s $s=$REGISTRY/$s:latest -n default
                            kubectl rollout status deployment/$s -n default
                        """
                    }
                }
            }
        }
    }
}