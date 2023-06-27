import { Component, OnInit } from '@angular/core';
import { Form, FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { ItemService } from './item.service';
import { nextUrl, redirectUrl } from './model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  userForm!: FormGroup
  user!: string
  nextUrl!: string
  authUrl!: string

  constructor(private fb: FormBuilder, private router: Router, private itmSvc: ItemService){}

  ngOnInit(): void {
      localStorage.clear()
      this.userForm = this.fb.group({
        userName: this.fb.control('')
      })
  }

  saveUser(){
    localStorage.clear()
    this.user = this.userForm.get("userName")?.value
    // console.log(this.user)
    // how can i share this info with other components?
    localStorage.setItem("user", this.user)
    this.router.navigate(["/category"])
  }

  callGDrive() {
    this.itmSvc.Gdrive(this.user)
      .then((result) => {
        // console.log(result);
        const uri = result as nextUrl;
        this.nextUrl = uri.nextUrl;
        console.log(this.nextUrl);
  
        if (this.nextUrl === "drive/signin"){

          console.log("need to sign in")

          const nextPage = "https://instinctive-celery-production.up.railway.app/api/" + this.nextUrl + "?user=" + this.user;
        

          // TODO: work on this, it seems to not match server controller
          // https://instinctive-celery-production.up.railway.app/api/drive/signin?user=kath
          this.itmSvc.getAuthUrl(nextPage)
            .then((url) => {
              const authUrl = url as redirectUrl;
              this.authUrl = authUrl.redirectUrl;
              console.log("auth url:" + this.authUrl);
              const popup = window.open(this.authUrl, "_blank", "width=500,height=600");
  
              if (popup){
              popup.addEventListener("load", () => {
                  popup.close();
            
                });
              }

              this.router.navigate(["/drive"]);
            
              })
            .catch((err) => console.log(err));
        } else {
          // navigate to drive component
          this.router.navigate(["/drive"]);
          
        }
      })
      .catch((err) => console.log(err));
  }
}


