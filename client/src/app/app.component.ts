import { Component, OnInit } from '@angular/core';
import { Form, FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  userForm!: FormGroup
  user!: string

  constructor(private fb: FormBuilder, private router: Router){}

  ngOnInit(): void {
      this.userForm = this.fb.group({
        userName: this.fb.control('')
      })
  }

  saveUser(){
    localStorage.clear()
    this.user = this.userForm.get("userName")?.value
    console.log(this.user)
    // how can i share this info with other components?
    localStorage.setItem("user", this.user)
    this.router.navigate(["/home"])
  }
}
