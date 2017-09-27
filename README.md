Welcome to CWIN 2017!
===================

This is Team One's repo, where we'll be designing, building and testing a [Amazon Rekognition](https://aws.amazon.com/rekognition/) system to record, index and then replay names of attendees from their faces.

## Routes

GET  - /getPicture -  Allows you to take a photo with the webcam, name the person who appears in the photo, upload to S3 and then index the photo in Rekognition

GET - /searchByPicture - Allows you to take a photo with the webcam and see if AWS Rekognition can recognise who is in it.

## Notes

* Connecting to AWS requires a users credentials. Once a user is made using AWS IAM you are given a "credentials.csv" file containing the relevant creds. Copy and pasting this csv file (without changing the format) into the top level directory will allow the app to access the creds.
* App requires an S3 bucket called "cwin17" and a Rekognition collection called "cwin17".
* The S3 bucket must be in the same region as Rekognition. Currently eu-west-1 (Ireland) is the closest region which has Rekognition.
