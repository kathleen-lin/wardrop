import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ItemService } from '../item.service';
import { analysisResult } from '../model';
import { PhotoService } from '../photo.service';

@Component({
  selector: 'app-analyse',
  templateUrl: './analyse.component.html',
  styleUrls: ['./analyse.component.css']
})
export class AnalyseComponent implements OnInit{

  fileName!: string
  itmDescription!: string

  constructor(private activatedRoute: ActivatedRoute, private itmSvc: ItemService, private photoSvc: PhotoService, private router: Router) {}

  ngOnInit(): void {
      this.activatedRoute.params.subscribe(
        async (params) => {
          this.fileName = params['filename']; 
          console.log(this.fileName)
          this.itmSvc.analyseImage(this.fileName)
            .then((result) => { 
              const analysisResult = result as analysisResult;
              this.itmDescription = analysisResult.itemDescription
              this.photoSvc.setItemDescription(this.itmDescription)
              this.router.navigate(["add/details"])
            })
            .catch((err) => console.log(err))
        }
          )
  
    }

}
