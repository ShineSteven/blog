package shine.st.blog.controllers

import play.api.mvc.{Action, Controller}
import shine.st.blog._
import shine.st.blog.common.ProviderContext
import shine.st.blog.services.PostService
import shine.st.common.aws.S3
import shine.st.common.{DateTimeUtils, IOUtils}

/**
  * Created by shinesteven on 2015/7/28.
  */
class IndexCtrl extends Controller with ProviderContext {
  def index = Action { implicit request =>
    val allPost = PostService.all
    Ok(shine.st.blog.views.html.index(allPost))
  }

  def aboutMe(typeName: String) = Action { implicit request =>
    config.getString(s"about.$typeName.file_name") match {
      case Some(fileName) =>
        val s3AboutMe = S3.getObject(blogBucketName, fileName)
        val meta = s3AboutMe.getObjectMetadata
        val content = IOUtils.inputStreamToString(s3AboutMe.getObjectContent)
//FIXME config to map
        Ok(shine.st.blog.views.html.about_me(config.getString(s"about.$typeName.title").get,(content, DateTimeUtils.getDateTimeOptFromDate(meta.getLastModified))))
      //      FIXME add reuse error page
      case None => Ok(shine.st.blog.views.html.about_me("error",("about me key wrod error", None)))
    }


  }


  def template = Action { implicit reqeust =>
    Ok(shine.st.blog.views.html.template("shine/st/blog/test"))
  }
}
