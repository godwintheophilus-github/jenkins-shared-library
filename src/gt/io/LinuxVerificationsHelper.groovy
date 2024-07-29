#!/usr/bin/groovy

package gt.io;

class LinuxVerificationsHelper implements Serializable {
    def steps
    def LinuxVerificationsHelper(steps) {
        this.steps = steps 
    }

    String checkWhoAmI(Map args) {
        steps.echo "Validating the variables ${args}"
        steps.sh "whoami"
    }
}