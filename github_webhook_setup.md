# Setting Up GitHub Webhooks for Automatic Jenkins Builds

This guide explains how to configure GitHub webhooks to automatically trigger your Jenkins pipeline when new commits are pushed to your repository.

## Prerequisites

- Administrator access to your GitHub repository
- Administrator access to your Jenkins instance
- Jenkins server accessible from the internet (or using a tunnel like ngrok for local development)

## Step 1: Configure Jenkins

1. **Install Required Plugins**:
   - Log in to Jenkins
   - Go to "Manage Jenkins" > "Manage Plugins"
   - In the "Available" tab, search for and install:
     - "GitHub Integration" plugin
     - "Generic Webhook Trigger" plugin
   - Check "Restart Jenkins when installation is complete"

2. **Configure GitHub Server in Jenkins**:
   - Go to "Manage Jenkins" > "Configure System"
   - Scroll to the "GitHub" section
   - Click "Add GitHub Server"
   - Name: `GitHub`
   - API URL: `https://api.github.com`
   - Credentials: Create or select a GitHub personal access token with `repo` and `admin:repo_hook` scopes
   - Test the connection, then save

3. **Update Pipeline Configuration**:
   - Open your Jenkinsfile and add the trigger configuration at the top level:

```groovy
pipeline {
    agent any
    
    triggers {
        githubPush()
    }
    
    // ... existing pipeline code
```

## Step 2: Configure GitHub Webhook

1. **Get Jenkins Webhook URL**:
   - Your webhook URL will be: `http://YOUR_JENKINS_URL/github-webhook/`
   - For local development using ngrok: `https://YOUR_NGROK_URL/github-webhook/`

2. **Add Webhook in GitHub**:
   - Go to your GitHub repository
   - Click "Settings" > "Webhooks" > "Add webhook"
   - Payload URL: Enter your Jenkins webhook URL
   - Content type: Select `application/json`
   - Secret: Leave empty (or create a secret if desired for additional security)
   - Which events would you like to trigger this webhook?
     - Select "Just the push event" (or choose specific events as needed)
   - Active: Check this box
   - Click "Add webhook"

3. **Verify Webhook**:
   - GitHub will send a ping event to verify the connection
   - Check "Recent Deliveries" in the webhook settings to confirm it was successful
   - You should see a green checkmark with a 200 response

## Step 3: Test the Integration

1. Make a small change to your repository (e.g., update a README file)
2. Commit and push the change to the branch you've configured in the Jenkins pipeline
3. Verify that Jenkins automatically starts a new build
4. Check the build logs to confirm it was triggered by the webhook

## Troubleshooting

- **Webhook Not Triggering**:
  - Verify Jenkins URL is publicly accessible
  - Check GitHub webhook delivery logs
  - Ensure the correct branch is being pushed to
  - Review Jenkins security settings (CSRF protection might block webhooks)

- **Authentication Issues**:
  - Verify your GitHub personal access token has not expired
  - Ensure token has appropriate permissions

- **Network Issues**:
  - If using a local Jenkins instance, consider using ngrok to expose it temporarily
  - Command: `ngrok http 8080` (replace 8080 with your Jenkins port)

## Security Considerations

- Limit webhook access to only necessary repositories
- Consider implementing a shared secret for webhook validation
- Regularly rotate GitHub personal access tokens
- Use HTTPS for all webhook communications

## Additional Configuration

For more advanced webhook triggers, such as filtering by specific file changes or branches, you can use the Generic Webhook Trigger plugin:

```groovy
triggers {
    GenericTrigger(
        genericVariables: [
            [key: 'ref', value: '$.ref'],
            [key: 'commit', value: '$.after']
        ],
        causeString: 'Triggered by GitHub push to $ref',
        token: 'your-token', // Use a unique token for this job
        printContributedVariables: true,
        printPostContent: true,
        regexpFilterText: '$ref',
        regexpFilterExpression: 'refs/heads/master' // Only trigger on master branch pushes
    )
}
``` 