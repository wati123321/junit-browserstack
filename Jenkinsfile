pipeline {
    agent any
    stages {
        stage('One') {
                steps {
                        echo 'Hello.. from stage One'
			
                }
        }
	    stage('Two'){
		    
		steps {
			input('Hello.. from stage Two')
        }
	    }
        stage('Three') {
                when {
                        not {
                                branch "master"
                        }
                }
                steps {
			echo "Hello.. from stage Three"
                        }
        }
        stage('Four') {
                parallel {
                        stage('Unit Test') {
                                steps{
                                        echo "Hello.. from stage Four.. Stage Unit Test"
                                }
                        }
                        stage('Integration test') {
                        agent {
                                docker {
                                        reuseNode false
					image 'ubuntu'
                                        }
			}
				steps {
					echo 'Hello.. from stage Four'
				}
                               
			}  }
        }
    }
}

