pipeline {
    agent any

    environment {
        REGISTRY = "localhost:5000"
        IMAGE = "my-microservice"
        KUBECONFIG = "/root/.kube/config"
    }

    triggers {
        pollSCM('H/30 * * * *') // Poll SCM
    }

    stages {
        stage('Clone') {
            steps {
                git branch: 'develop',
                    credentialsId: 'github-token',
                    url: 'https://github.com/Ivaskaa/social-microservices.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t $REGISTRY/$IMAGE:latest .
                    docker push $REGISTRY/$IMAGE:latest
                """
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                    kubectl set image deployment/$IMAGE $IMAGE=$REGISTRY/$IMAGE:latest -n default
                    kubectl rollout status deployment/$IMAGE -n default
                """
            }
        }
    }
}