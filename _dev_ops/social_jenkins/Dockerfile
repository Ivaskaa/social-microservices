FROM jenkins/jenkins:lts-jdk17

USER root

# Install docker CLI and kubectl
RUN apt-get update && \
    apt-get install -y docker.io curl && \
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl && \
    apt-get clean

# Jenkins runs as root to access docker.sock
USER root