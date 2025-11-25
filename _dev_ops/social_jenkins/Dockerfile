FROM jenkins/jenkins:lts-jdk17

USER root

# Install docker CLI
RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean

# Install kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl

# Jenkins should run as root to access docker.sock
USER root