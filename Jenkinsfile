pipeline {
    agent any

    tools {
      jdk 'Java16'
    }

    stages {
        stage('Build') {
            steps {
                sh script: 'chmod +x gradlew'
                sh label: 'Gradle Build', script: './gradlew --parallel --max-workers=8  clean bundleRelease assembleRelease'
            }
        }
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'app/build/outputs/bundle/release/app-release.aab', followSymlinks: false, onlyIfSuccessful: true
                archiveArtifacts artifacts: 'app/build/outputs/apk/release/app-release.apk', followSymlinks: false, onlyIfSuccessful: true
                archiveArtifacts artifacts: 'app/build/outputs/mapping/release/mapping.txt', followSymlinks: false, onlyIfSuccessful: true
                archiveArtifacts artifacts: 'wear/build/outputs/bundle/release/wear-release.aab', followSymlinks: false, onlyIfSuccessful: true
                archiveArtifacts artifacts: 'wear/build/outputs/apk/release/wear-release.apk', followSymlinks: false, onlyIfSuccessful: true
                archiveArtifacts artifacts: 'wear/build/outputs/mapping/release/mapping.txt', followSymlinks: false, onlyIfSuccessful: true
            }
        }
	    stage('Publish to Play Store') {
		    steps {
			    script {
                    if (env.BRANCH_NAME == 'master') {
                        echo 'Publishing Bundle to Beta channel (Not allowed to fail)'
                        androidApkUpload deobfuscationFilesPattern: 'app/build/outputs/mapping/release/mapping.txt,wear/build/outputs/mapping/release/mapping.txt', filesPattern: 'app/build/outputs/bundle/release/app-release.aab,wear/build/outputs/bundle/release/wear-release.aab', googleCredentialsId: 'Florianisme', rolloutPercentage: '20', trackName: 'beta'
                    } else if (env.BRANCH_NAME == 'release') {
                        echo 'Publishing Bundle to Internal channel (Not allowed to fail)'
                        androidApkUpload deobfuscationFilesPattern: 'app/build/outputs/mapping/release/mapping.txt,wear/build/outputs/mapping/release/mapping.txt', filesPattern: 'app/build/outputs/bundle/release/app-release.aab,wear/build/outputs/bundle/release/wear-release.aab', googleCredentialsId: 'Florianisme', rolloutPercentage: '100', trackName: 'internal'
                    } else if (env.BRANCH_NAME == 'develop' || env.BRANCH_NAME.contains('feature')) {
                        echo 'Publishing Bundle to Internal channel (Allowed to fail)'
                        try {
                            androidApkUpload deobfuscationFilesPattern: 'app/build/outputs/mapping/release/mapping.txt,wear/build/outputs/mapping/release/mapping.txt', filesPattern: 'app/build/outputs/bundle/release/app-release.aab,wear/build/outputs/bundle/release/wear-release.aab', googleCredentialsId: 'Florianisme', rolloutPercentage: '100', trackName: 'internal'
                        } catch(error) {
                            currentBuild.result = 'SUCCESS'
                        }
                    } else {
                        echo 'Publishing criteria not met'
                    }
                }
            }
		}
    }
	post {
		failure {
			script {
				currentBuild.result = 'FAILURE'
			}
		}
		unstable {
			script {
				currentBuild.result = 'UNSTABLE'
			}
		}
		always {
			step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: emailextrecipients([[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']])])
		}
	}
}