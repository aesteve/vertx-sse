pipeline {
    agent any
    stages {
        stage ('Clone sources') {
            steps {
                checkout scm
                prepareGradleInit()
            }
        }

        stage ('Gradle build') {
            steps {
                script { gradleBuild "clean", "build", "check", "assemble" }
            }
        }

        stage ('trigger deployment') {
            steps {
                noReview {
                    script { gradlePublish "assemble" }
                }
            }
        }
    }
}
