import Option from '@/models/management/Option';
import Image from '@/models/management/Image';

export default class ProposedQuestion {
  id: number | null = null;
  title: string = '';
  content: string = '';
  image: Image | null = null;
  username: string = '';
  status: string = '';
  justification: string = '';

  options: Option[] = [new Option(), new Option(), new Option(), new Option()];

  constructor(jsonObj?: ProposedQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.content = jsonObj.content;
      this.image = jsonObj.image;
      this.username = jsonObj.username;
      this.status = jsonObj.status;
      this.justification = jsonObj.justification;

      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
    }
  }
}
