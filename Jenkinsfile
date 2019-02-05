pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
                TELEGRAM = credentials('telegram')
            }
      steps {
            sh ('./build.sh')
      }
    }
    stage('Test') {
      steps {
        echo 'Testing..'
      }
    }
    stage('Deploy') {
      steps {
        echo 'Deploying....'
      }
    }
  }
}
