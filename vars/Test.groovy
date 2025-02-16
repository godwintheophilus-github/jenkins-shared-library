#!/usr/bin/groovy

import gt.io.LinuxVerificationsHelper

def call(body) {

    def helper = new LinuxVerificationsHelper(this)
    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent any
        options {
            ansiColor('xterm')
            timestamps()
        }
        stages {
            stage('testing') {
                steps {
                    echo 'Building..'
                    script {
                        // environment {
                        //     BRANCH_NAME = "${params.branch}"
                        // }
                        helper.checkWhoAmI(env: params.env, 
                        branch: params.branch)
                        sh "whoami"
                        echo "${env.WORKSPACE}"
                    }
                }
            }
        }
    }
}