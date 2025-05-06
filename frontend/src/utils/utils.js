import {jwtDecode} from "jwt-decode";

export const zeroFill = (number, width) => {
  const numberAsString = number.toString();
  return numberAsString.length >= width ? numberAsString : new Array(width - numberAsString.length + 1).join('0') + numberAsString;
};

export const dateFormat = (date, format) => {
  if (!date) return " ";

  const d = new Date(date);
  const weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];

  const replacements = {
    "yyyy": d.getFullYear(),
    "yy": zeroFill(d.getFullYear() % 1000, 2),
    "MM": zeroFill(d.getMonth() + 1, 2),
    "dd": zeroFill(d.getDate(), 2),
    "E": weekName[d.getDay()],
    "HH": zeroFill(d.getHours(), 2),
    "hh": zeroFill(d.getHours() % 12 || 12, 2),
    "mm": zeroFill(d.getMinutes(), 2),
    "ss": zeroFill(d.getSeconds(), 2),
    "a/p": d.getHours() < 12 ? "오전" : "오후"
  };

  return format.replace(/(yyyy|yy|MM|dd|E|HH|hh|mm|ss|a\/p)/g, (match) => replacements[match] || match);
};

export const isEmptyOrNull = (value) => {
  if (value === null || value === undefined) {
    return true;
  }

  if (Array.isArray(value) && value.length === 0) {
    return true; // 배열이고 length가 0인 경우, 비어있음
  }

  if (typeof value === 'number') {
    return false; // 숫자일 때는 예외처리, 비어있지 않음
  }

  if (typeof value === 'string') {
    // 문자열의 trim() 함수를 사용하여 양쪽 공백을 제거한 뒤, 비어있는지 확인
    return value.trim() === '';
  }

  // 다른 데이터 유형에 대한 처리도 추가할 수 있음

  return false; // 기타 경우, 비어있지 않음
}

export const useEnterKeySubmit = (callback) => {
  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      callback();
    }
  };

  return handleKeyDown;
};


export const buildQuery = (params) => {
  // params 객체를 쿼리 문자열 배열로 변환
  const queryParts = Object.entries(params)
  .filter(([key, value]) => !isEmptyOrNull(value)) // 값이 null, undefined, 빈 문자열이 아닌 경우만 처리
  .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`);

  return `?${queryParts.join('&')}`;
}

export const isTokenExpired = (token) => {
  if (!token) {
    return true; // 토큰이 없으면 만료된 것으로 간주
  }

  const decoded = jwtDecode(token);
  const currentTime = Date.now() / 1000; // 현재 시간 (초 단위)
  if (decoded.exp < currentTime) {
    return true; // 토큰이 만료됨
  }

  return false; // 토큰이 유효함
};

export const containsRole = (roles, roleName) => {
  return roles.some(role => role === roleName);
}

// 데이터 용량 포메팅 함수
export const formatBytes = (bytes) => {
  if (typeof bytes !== 'number' || isNaN(bytes) || bytes < 0) {
    return '-';
  }

  if (bytes < 1024) {
    return `${bytes} BYTE`;
  } else if (bytes < 1024 * 1024) {
    return `${(bytes / 1024).toFixed(2)} KB`;
  } else if (bytes < 1024 * 1024 * 1024) {
    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
  } else if (bytes < 1024 * 1024 * 1024 * 1024) {
    return `${(bytes / (1024 * 1024 * 1024)).toFixed(2)} GB`;
  } else {
    return `${(bytes / (1024 * 1024 * 1024 * 1024)).toFixed(2)} TB`;
  }
}

//재귀적 구조로 되어 있는 배열을 1차원 배열로 flat하게 만드는 유틸
//첫번째 인자로 배열을 받고 두번째 인자로 재귀적으로 들어가는 key에 해당하는
//어트리뷰트 이름을 받는다.아래와 같은 데이터는 key값이 subMenuList이다.
//[
//     {
//         "siteMenuId": 142,
//         "subMenuList": [
//             {
//                 "siteMenuId": 155,
//                 "subMenuList": []
//             },
//             {
//                 "siteMenuId": 156,
//                 "subMenuList": []
//             }
//         ]
//     },
//     {
//         "siteMenuId": 144,
//         "subMenuList": [
//             {
//                 "siteMenuId": 160,
//                 "subMenuList": []
//             },
//             {
//                 "siteMenuId": 161,
//                 "subMenuList": []
//             },
//             {
//                 "siteMenuId": 162,
//                 "subMenuList": []
//             }
//         ]
//     },
// ]
export const flattenRecursive = (array, key = 'children') => {
  return array.reduce((acc, item) => {
    // 현재 객체를 결과에 추가
    acc.push(item);

    // 재귀적으로 children을 평탄화하여 추가
    if (Array.isArray(item[key])) {
      acc = acc.concat(flattenRecursive(item[key], key));
    }

    return acc;
  }, []);
}

/**
 * Quill 리스트를 그룹화하는 함수
 * @param {string} htmlContent - Quill 에디터에서 저장된 HTML 문자열
 * @returns {string} - 그룹화된 HTML 문자열
 */
export const groupQuillLists = (htmlContent) => {
  const parser = new DOMParser();
  const doc = parser.parseFromString(htmlContent, 'text/html');
  const listItems = Array.from(doc.querySelectorAll('li'));

  let currentList = null; // 현재 그룹화된 <ol>
  let previousIndent = null; // 이전 indent 레벨

  listItems.forEach((li) => {
    const indentClass = Array.from(li.classList).find((cls) =>
        cls.match(/^ql-indent-\d+$/)
    );

    const parentTag = li.parentElement.tagName.toLowerCase(); // 부모 태그 확인 (ol 또는 ul)

    if (indentClass) {
      // 현재 indent가 이전 indent와 다르면 새로운 <ol> 또는 <ul> 시작
      if (indentClass !== previousIndent) {
        // 부모 태그가 ol이면 새로운 ol 생성, ul이면 새로운 ul 생성
        currentList = document.createElement(parentTag === 'ul' ? 'ul' : 'ol');
        currentList.classList.add(indentClass);
        li.before(currentList); // 현재 <li> 앞에 새로운 <ol> 또는 <ul> 삽입
      }

      currentList.appendChild(li); // <li>를 현재 <ol> 또는 <ul>에 추가
      previousIndent = indentClass; // 이전 indent 업데이트
    } else {
      // indent 클래스가 없으면 그룹화를 종료
      currentList = null;
      previousIndent = null;
    }
  });

  return doc.body.innerHTML;
};

export const getRandomInt = (min, max) => {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}