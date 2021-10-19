import jenkins.model.Jenkins
import org.jenkinsci.plugins.github.GitHubPlugin   // setup plugin
import org.jenkinsci.plugins.github.config.GitHubServerConfig
import jenkins.model.*
import com.cloudbees.hudson.plugins.folder.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import jenkins.plugins.git.GitSCMSource
import jenkins.plugins.git.traits.BranchDiscoveryTrait
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.FolderLibraries




// setup global admin email

println "--> setting admin email"
def adminEmail = 'example@example.com'
def jlc = JenkinsLocationConfiguration.get()
jlc.setAdminAddress(adminEmail)
jlc.save()



// import for jenkins system message

def systemMessage = "Configuration from groovu script"
Jenkins jenkins = Jenkins.getInstance()
jenkins.setSystemMessage(systemMessage)
jenkins.save()


// setup smtp server

println "--> setting SMTP server"
def SMTPHost = 'smtp.vlad.com'
def mailServer = instance.getDescriptor("hudson.tasks.Mailer")
mailServer.setSmtpHost(SMTPHost)
instance.save()


// setup slack
println "--> setting Slack"
def slack = Jenkins.instance.getExtensionList('jenkins.plugins.slack.SlackNotifier\$DescriptorImpl')[0]
slack.tokenCredentialId = 'slack-token'
slack.teamDomain = 'Jenkins-slaves'
slack.room = '#testing-jenkins'
slack.save()
println 'Slack global settings configured.'


// setup github

println "--> setting Github"
GitHubServerConfig server = new GitHubServerConfig('')
server.setName('GitHubAPI')
server.setApiUrl('https://api.github.com')
GitHubPlugin.configuration().getConfigs().add(server)


//  install plugins in list

def pluginParameter="gitlab-plugin hipchat swarm "
def plugins = pluginParameter.split()
println(plugins)
def instance = Jenkins.getInstance()
def pm = instance.getPluginManager()
def uc = instance.getUpdateCenter()
def installed = false

plugins.each {
  if (!pm.getPlugin(it)) {
    def plugin = uc.getPlugin(it)
    if (plugin) {
      println("Installing " + it)
      plugin.deploy()
      installed = true
    }
  }
}

instance.save()
if (installed)
instance.doSafeRestart()


// Create folders

Jenkins jenkins = Jenkins.instance // saves some typing
String folderName = "folder1"
String folderName2 = "folder2"
String folderName3 = "folder3"
String folderNameTemp = folderName + "-folder"

def folder = jenkins.getItem(folderName)
def folder3 = jenkins.getItem(folderName3)
if (folder == null &&  folder3 == null ) {
  // Create the folder if it doesn't exist or if no existing job has the same name
  folder  = jenkins.createProject(Folder.class, folderName)
  folder2 = folder.createProject(Folder.class, folderName2)
  folder3 = jenkins.createProject(Folder.class, folderName3)
} else {
  if (folder.getClass() != Folder.class) {
    // when folderName exists, but is not a folder we make the folder with a temp name
    folder = jenkins.createProject(Folder.class, folderNameTemp)
    // Move existing jobs from the same environment to folders (preseve history)
    Item[] items = jenkins.getItems(WorkflowJob.class)
    def job_regex = "^" + folderName

    items.grep { it.name =~ job_regex }.each { job ->
      Items.move(job, folder)
    }

    // Rename the temp folder now we've moved the jobs
    folder.renameTo(folderName)
  }
}


// Add shared library

println("Adding libraries")
List libraries = [] as ArrayList
[
    "jenkins-shared-lib":"https://github.com/vlddryga2233/jenkins-library.git" // 7
]

def library_version   = "main" // 7
def library_cred_name = "vlddryga2233" // 7
def library_cred_user = "vlddryga2233" // 7
def library_cred_key  = ""

def libConfig(name, remote, library_version, library_cred_name) {
    def scm = new GitSCMSource(remote)
    scm.credentialsId = library_cred_name
    scm.traits = [new BranchDiscoveryTrait()]
    def retriever = new SCMSourceRetriever(scm)
    def library = new LibraryConfiguration(name, retriever)
    library.defaultVersion = library_version
    library.implicit = false
    library.allowVersionOverride = true
    library.includeInChangesets = false
    return library
}

libraries.each {name, remote ->
  libraries << libConfig(name, remote, library_version, library_cred_name)
}

Folder fold_lib = Jenkins.instance.getItemByFullName(folderName)
fold_lib.getProperties().add(new FolderLibraries(libraries))
fold_lib.save()



// Adding credentials 
// Need to add VAULT for security 

def secret_name   = "temp_user_pass" 
def secret_description   = "some temporary name nad pass" 
def username_cred     = "user_temp" 
def password_cred     = "password_temp" 


// Username and Password credentiasl

usernamePassword = new usernamePasswordCredentialsImpl(
  CredentialsScope.GLOBAL,
  secret_name,
  secret_description,
  username_cred,
  password_cred
)
store.addCredentials(domain,usernamePassword)


// Ssh private key credentials

privateKey =new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(library_cred_key)

sshKey = new BasicSSHUserPrivateKey(
  CredentialsScope.GLOBAL,
  library_cred_name,
  library_cred_user,
  privateKey,
  "",
  ""
)

store.addCredentials(domain,sshKey)
jenkins.save()


// Adding roles

def globalRoleRead = "read"
def globalBuildRole = "build"
def globalRoleAdmin = "admin"
def folderRoleAdmin = "poweruser"

def adminPermissions = [
"hudson.model.Hudson.Administer",
"hudson.model.Hudson.Read"
]

def readPermissions = [
"hudson.model.Hudson.Read",
"hudson.model.Item.Discover",
"hudson.model.Item.Read"
]

def buildPermissions = [
"hudson.model.Hudson.Read",
"hudson.model.Item.Build",
"hudson.model.Item.Cancel",
"hudson.model.Item.Read",
"hudson.model.Run.Replay"
]

def folderPermissions = [
"hudson.model.Hudson.Administer",
"hudson.model.Hudson.Read"
]

def roleBasedAuthenticationStrategy = new RoleBasedAuthorizationStrategy()
jenkins.setAuthorizationStrategy(roleBasedAuthenticationStrategy)

Set<Permission> adminPermissionSet = new HashSet<Permission>()
adminPermissions.each { p ->
  def permission = Permission.fromId(p)
  if (permission != null) {
    adminPermissionSet.add(permission)
  } else {
    println("${p} is not a valid permission ID (ignoring)")
  }
}

Set<Permission> buildPermissionSet = new HashSet<Permission>()
buildPermissions.each { p ->
  def permission = Permission.fromId(p)
  if (permission != null) {
    buildPermissionSet.add(permission)
  } else {
    println("${p} is not a valid permission ID (ignoring)")
  }
}

Set<Permission> readPermissionSet = new HashSet<Permission>()
readPermissions.each { p ->
  def permission = Permission.fromId(p)
  if (permission != null) {
    readPermissionSet.add(permission)
  } else {
    println("${p} is not a valid permission ID (ignoring)")
  }
}

Set<Permission> folderRoleAdminSet = new HashSet<Permission>()
folderPermissions.each { p ->
  def permission = Permission.fromId(p)
  if (permission != null) {
    folderRoleAdminSet.add(permission)
  } else {
    println("${p} is not a valid permission ID (ignoring)")
  }
}

// admins
Role adminRole = new Role(globalRoleAdmin, adminPermissionSet)
roleBasedAuthenticationStrategy.addRole(RoleType.Global, adminRole)

// builders
Role buildersRole = new Role(globalBuildRole, buildPermissionSet)
roleBasedAuthenticationStrategy.addRole(RoleType.Global, buildersRole)

// anonymous read
Role readRole = new Role(globalRoleRead, readPermissionSet)
roleBasedAuthenticationStrategy.addRole(RoleType.Global, readRole)

Role folderRole = new Role(folderRoleAdmin, folderRoleAdminSet)
roleBasedAuthenticationStrategy.addRole(RoleType.Global, folderRole)

access.admins.each { l ->
  println("Granting admin role to ${l}")
  roleBasedAuthenticationStrategy.assignRole(RoleType.Global, adminRole, l)
}

access.builders.each { l ->
  println("Granting builder role to ${l}")
  roleBasedAuthenticationStrategy.assignRole(RoleType.Global, buildersRole, l)
}

access.readers.each { l ->
  println("Granting read role to ${l}")
  roleBasedAuthenticationStrategy.assignRole(RoleType.Global, readRole, l)
}

access.poweruser.each { l ->
  println("Granting admin folder role to ${l}")
  roleBasedAuthenticationStrategy.assignRole(RoleType.Global, folderRole, l)
}

jenkins.save()
jenkinsLocationConfiguration.save()