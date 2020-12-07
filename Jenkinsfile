
@Library(['pipeline-framework','pipeline-toolbox']) _
properties([disableConcurrentBuilds()])

def JAR_FILE = null

workflow("gradle", [

   gradle_buildandtest: {
        sh "chmod +x ./gradlew"
        def date = new Date().format("yy.MM.ddHHmmss", TimeZone.getTimeZone("UTC"))
        def version = sh (script: "./gradlew properties -q | grep '^version:' | awk '{print \$2}'",
                        returnStdout: true).trim()
        def archiveBaseName = sh (script: "./gradlew properties -q | grep 'archivesBaseName:' | awk '{print \$2}'",
                        returnStdout: true).trim()
        if (isPullRequest()) {
            buildVersion = 'PR' + env.CHANGE_ID + '-' + version + '-'+ date
        }else{
            buildVersion = version + '-'+ date
        }
        JAR_FILE = './build/libs/' + archiveBaseName + '-' + buildVersion + '.jar'
        logInfo { "jar file name: "+ jarName }
        sh "./gradlew -Pversion=${buildVersion} clean build test"
   },

   gradle_buildandtest_after: [
       ['CodeChecks', {
           execParallel([
               "CodeQuality" : {
                   sh "apk add --no-cache git"
                   sh "./gradlew jacocoTestReport"
                   sonarRunner(context)
               },
               "CodeSecurity": {
                   performBinaryApplicationScan()
               }
           ])
       }]
   ],

   gradle_publish: {
        script {
            env.DEPL_NAME=sh( script: "echo notify-${env.BRANCH_NAME} |tr -d '_'| tr '[:upper:]' '[:lower:]'",returnStdout: true).trim()
        }
        withCredentials([usernamePassword(credentialsId: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USER'),string
        (credentialsId: 'KUBECTL_CCBD', variable: 'kube_config')]) {
            execParallel([
                "Push image to Repo" : {
                    if (env.BRANCH_NAME == 'master') {
                        sh "./gradlew jib -Djib.to.image=<nexus.repo.url>:9343/${env.DEPL_NAME}:${env.BUILD_ID} -Djib.to.auth.username=${nexususer}
                        -Djib.to.auth.password=${nexuspassw}"
                    }
                },
                "Create k8s config file" : {
                    sh "echo -n ${kube_config} > kubeconfig"
                    sh "base64 -d kubeconfig > kubeconf"
                },

                "Download kubectl and deploy new image": {
                    if (env.BRANCH_NAME == 'master') {
                        sh "wget https://storage.googleapis.com/kubernetes-release/release/v1.18.0/bin/linux/amd64/kubectl"
                        sh "chmod +x kubectl"
                    }
                }
            ])
        }
        echo "Deploy"
        if (env.BRANCH_NAME == 'master') {
            sh  "./kubectl --kubeconfig kubeconf set image deployments/${env.DEPL_NAME} notify=<nexus.repo.url>:9343/${env.DEPL_NAME}:${env
            .BUILD_ID}"
        }
   }
])