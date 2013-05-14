Performing a Release
====================

# Steps

Follow these simple steps to cut a release of the HTTP stub server.

## 1. Create Branch

This project is so simple that there's no point creating release candiates etc. Just make sure all tests are passing and create a new branch. 

For example, for release '2.34':

  $ git checkout -b release-2.34

  **Note:** If you're increasing the major version number, remember to update the master version as well (eg, if releasing 2.34, master version should be 2.0-SNAPSHOT)

## 2. Update the POMs

Update the Maven POMs:

  $ mvn versions:set -DgenerateBackupPoms=false -DnewVersion=1.23

## 3. Push the branch back to GitHub

Remember to add the changed POMs first.

  $ git add .
  $ git push origin release-1.23

## 4. (Optional) Deploy to your artifact repository

There is currently no public repository for this project, so it has to be published locally.

  $ mvn deploy -DaltDeploymentRepository=nexus::default::http://nexus.company.com:8081/nexus/content/repositories/releases 

