# queryservice

## Setup

### 1. Setup GraphDB Repository
* Create an Account and Download [GraphDB Free](https://www.ontotext.com/products/graphdb/graphdb-free/).
* Start GraphDB Free and make sure the port is set to 7200 (should be by default), Press the Settings button to check the port.
* Download the [Star Wars RDF data](https://swapi-blog-post.s3-eu-west-1.amazonaws.com/data.ttl), which we have already trained a model for.
* Make sure GraphDB is up and running, and it should prompt you to [localhost:7200](localhost:7200).
* Go to Import-RDF-"Create new repository".
* Give the Repository an ID, and set Ruleset to "No inference".
* Now, go to Import-RDF and "Upload RDF files", and choose the "data.ttl", and click Import.

### 2. Setup Application Properties
* In the project go to resources/application.properties.
* Change the repoURL to http://localhost:7200/repositories/REPO_ID, where REPO_ID is the name of your repository.
* All the other properties are set by default to work with the Star Wars repository.

### 3. Setup VM Options
* The program needs atleast 4Gb of memory to run. This is due to the amount of CoreNLP annotators loaded into the pipeline.
* Add the following Java VM options: **-Xmx4g -Xms4g**


## Using the NLI
To use the Natural Language Interface, start the program and wait for it to load the properties and start the server.  
Go to [localhost:8080](localhost:8080) and you should be prompted to the web page.  
If everything is working, the web page should say **Connected**.  

The model has not been train on large amounts of data, so it will not recognize many Star Wars entities.  
We used the movie script from the first Star Wars movie "A New Hope" to train the model, which means the NLP will  
only recognize certain entities that appear frequently in the first movie, for example: Darth Vader, Tatooine, Han Solo etc.  

## Example Queries