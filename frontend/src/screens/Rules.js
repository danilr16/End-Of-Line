import React from "react";
import "../static/css/home/home.css";

const Rules = () => {
  return (
    <div className="pdf-container">
      <iframe
          src="/Rules.pdf"
          title="Rules"
          width="100%"
          height="100%"
        />
    </div>
  );
};
export default Rules;
