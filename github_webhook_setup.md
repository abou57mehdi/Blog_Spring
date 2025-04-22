# Setting Up GitHub Webhooks for Jenkins on Localhost

This guide explains how to automatically trigger your Jenkins pipeline on a localhost installation when new commits are pushed to your GitHub repository.

## Document Placement

This documentation should be included in your project as follows:
- Place in the `/docs/ci-cd/` directory of your project
- Add to the table of contents in your main documentation
- Link from your main `README.md` in the "Development Workflow" section
- Reference in onboarding documentation for new developers

## Localhost Jenkins Solution Using ngrok

Since your Jenkins instance runs on localhost and is not directly accessible from the internet, GitHub webhooks cannot reach it. We'll use ngrok to create a secure tunnel.

### Step 1: Set Up ngrok

1. **Install ngrok**:
   - Download from [https://ngrok.com/download](https://ngrok.com/download)
   - Extract the executable to a convenient location
   - Optional: Add to your PATH for easy access

2. **Start ngrok tunnel**:
   ```bash
   # Assuming Jenkins runs on port 8080
   ngrok http 8080
   ```

3. **Note your temporary URL**:
   - ngrok will display a URL like `https://a1b2c3d4.ngrok.io`
   - This URL will forward to your localhost Jenkins
   - Keep this terminal window open while working with webhooks

### Step 2: Configure Jenkins for GitHub Integration

1. **Install Required Plugins**:
   - Log in to Jenkins at `http://localhost:8080`
   - Go to "Manage Jenkins" > "Manage Plugins"
   - In the "Available" tab, search for and install:
     - "GitHub Integration" plugin
     - "Generic Webhook Trigger" plugin
   - Restart Jenkins when prompted

2. **Configure GitHub Server in Jenkins**:
   - Go to "Manage Jenkins" > "Configure System"
   - Scroll to the "GitHub" section
   - Click "Add GitHub Server"
   - Name: `GitHub`
   - API URL: `https://api.github.com`
   - Credentials: Click "Add" to create a new credential
     - Kind: Username with password
     - Username: Your GitHub username (e.g., abou57mehdi)
     - Password: Your GitHub Personal Access Token (not your GitHub password)
     - ID: auto-generated or custom (e.g., "github-access-token")
     - Description: "GitHub API Token"
   - Select your credential from the dropdown
   - Test the connection to confirm it works
   - Save the configuration

3. **Update Jenkinsfile**:
   - The necessary trigger has already been added to your Jenkinsfile:
   ```groovy
   triggers {
       githubPush()
   }
   ```

### Step 3: Configure GitHub Webhook

1. **Set up webhook in GitHub**:
   - Go to your GitHub repository
   - Navigate to Settings → Webhooks → Add webhook
   - Set Payload URL to: `https://YOUR_NGROK_URL/github-webhook/` (use the URL from ngrok)
   - Content type: `application/json`
   - Select "Just the push event"
   - Check "Active"
   - Click "Add webhook"

2. **Test the webhook**:
   - GitHub will send a ping event
   - Check "Recent Deliveries" in webhook settings
   - You should see a green checkmark with a 200 response

### Step 4: Make a Test Commit

1. Make a small change to your repository
2. Commit and push to GitHub
3. Verify Jenkins starts a build automatically
4. Check build logs to confirm it was triggered by webhook

## Important Notes for Localhost Development

- **Temporary URL**: The ngrok URL changes each time you restart ngrok unless you have a paid plan
- **Session Duration**: Free ngrok sessions expire after a few hours
- **Webhook Updates**: You'll need to update the GitHub webhook URL when your ngrok URL changes
- **Alternative Approach**: For regular development without internet-accessible Jenkins, consider using `pollSCM` instead:
  ```groovy
  triggers {
      pollSCM('H/15 * * * *') // Poll every 15 minutes
  }
  ```

## Production Environment Recommendations

When moving to production:

1. **Host Jenkins on a server with a public IP address**
2. **Use a proper domain name with SSL certificate**
3. **Implement webhook secret for additional security**
4. **Create a dedicated GitHub service account for Jenkins integration**

## Integration with Your Existing Pipeline

This webhook triggering mechanism integrates with your existing CI/CD pipeline:

1. Code is pushed to GitHub repository
2. GitHub sends webhook notification to ngrok URL
3. ngrok forwards the request to your local Jenkins
4. Jenkins triggers the pipeline defined in your Jenkinsfile
5. All stages run: build, test, analysis, package, and deployment
6. Results are visible in Jenkins dashboard

This completes your CI/CD workflow automation, allowing for continuous integration and delivery triggered directly by code changes. 