#!/usr/bin/groovy

import gt.io.LinuxVerificationsHelper

def call(body) {

    def helper = LinuxVerificationsHelper(this)
    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
    String environments
    if((pipelineParams.env == 'dev') || (pipelineParams.env == 'qa') || (pipelineParams.env == 'prod') || (pipelineParams.env == 'stage')) {
        environments = pipelineParams.env
    } else {
        environments = 'dev'
    }
    if(environments == 'dev') {
        pipeline {
            agent any
            options {
                ansiColor('xterm')
                timestamps()
            }
            parameters {
                choice(name: 'env', choices: ['dev', 'qa', 'prod', 'stage'], description: 'Environment to deploy')
                string(name: 'branch', defaultValue: 'master', description: 'Branch to build')

            }
            stages {
                stage('Build') {
                    
                    steps {
                        echo 'Building..'
                        script {
                            environment {
                                BRANCH_NAME = "${params.branch}"
                            }
                            helper.checkWhoAmI(env: params.env, 
                            branch: params.branch)
                            sh "whoami"
                            echo "${env.WORKSPACE}"
                        }
                    }
                    post {
                        always {
                            echo 'This will always run'
                            script {
                                helper.checkWhoAmI(env: params.env, branch: params.branch)
                            }
                        }
                        success {
                            echo 'This will run only if successful'
                        }
                        failure {
                            echo 'This will run only if failed'
                        }
                        unstable {
                            echo 'This will run only if the run was marked as unstable'
                        }
                        changed {
                            echo 'This will run only if the state of the Pipeline has changed'
                            echo 'For example, if the Pipeline was previously failing but is now successful'
                        }
                    }
                }
                stage('Test') {
                    when {
                        expression {
                            BRANCH_NAME == 'master'
                        }
                    }
                    steps {
                        echo 'Testing..'
                    }
                }
                stage('Deploy') {
                    when { branch 'master' }
                    steps {
                        echo 'Deploying....'
                    }
                }
            }
        }
    }
}